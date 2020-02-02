package client.module.midi

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}

import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import javax.sound.midi.{MidiMessage, Receiver, ShortMessage}
import midi.read.Midi
import music.model.record.{MillisecondPosition, RawMidiNote}
import music.primitives.{Loudness, MidiNoteNumber}
import protocol.client.api.ClientInterface
import server.actions.recording.RecordNote

class MidiRecorder @Inject() (
  client: ClientInterface
) extends Thread with Receiver with LazyLogging {

  private val active: AtomicBoolean = new AtomicBoolean(false)
  private val queue: BlockingQueue[(MidiMessage, Long)] = new LinkedBlockingQueue[(MidiMessage, Long)]()

  def activate(): Unit = active.set(true)
  def deactivate(): Unit = active.set(false)

  override def run(): Unit = {
    try {
      while ( ! isInterrupted ) {
        val (message, microsecondTimestamp) = queue.take()
        if (active.get()) {
          message match {
            case msg: ShortMessage if Midi.IsNoteOn(msg) =>
              val note = RawMidiNote(
                MidiNoteNumber(msg.getData1),
                Loudness(msg.getData2),
                MillisecondPosition.fromMicroseconds(microsecondTimestamp)
              )
              client.sendCommand(RecordNote(note)) match {
                case None =>
                case Some(ex) => logger.error("Unable to send recorded note", ex)
              }
            case _ =>
          }
        }
      }
    } catch {
      case _: InterruptedException => logger.info("Midi Recorder was interrupted")
    }
  }

  override def send(message: MidiMessage, microsecondPosition: Long): Unit = {
    val elem = (message, microsecondPosition)
    queue.offer(elem)
  }

  override def close(): Unit = interrupt()

}
