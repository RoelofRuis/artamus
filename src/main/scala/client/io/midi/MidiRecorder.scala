package client.io.midi

import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}

import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import javax.sound.midi.{MidiMessage, Receiver, ShortMessage}
import music.model.record.RawMidiNote
import music.primitives.{Loudness, MidiNoteNumber, MillisecondPosition}
import protocol.client.api.ClientInterface
import server.actions.recording.RecordNote

class MidiRecorder @Inject() (
  client: ClientInterface
) extends Thread with Receiver with LazyLogging {

  private val queue: BlockingQueue[(MidiMessage, Long)] = new LinkedBlockingQueue[(MidiMessage, Long)]()

  override def run(): Unit = {
    try {
      while ( ! isInterrupted ) {
        val (message, timestamp) = queue.take()
        message match {
          case msg: ShortMessage if msg.getCommand == ShortMessage.NOTE_ON =>
            val note = RawMidiNote(
              MidiNoteNumber(msg.getData1),
              Loudness(msg.getData2),
              MillisecondPosition(timestamp)
            )
            client.sendCommand(RecordNote(note)) match {
              case None =>
              case Some(ex) => logger.error("Unable to send recorded note", ex)
            }
          case _ =>
        }
      }
    } catch {
      case _: InterruptedException => logger.info("Midi Recorder was interrupted")
    }
  }

  override def send(message: MidiMessage, timeStamp: Long): Unit = {
    val elem = (message, timeStamp)
    queue.offer(elem)
  }

  override def close(): Unit = interrupt()

}
