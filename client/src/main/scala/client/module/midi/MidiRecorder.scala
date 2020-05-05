package client.module.midi

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}

import client.infra.Client
import com.typesafe.scalalogging.LazyLogging
import nl.roelofruis.artamus.core.api.Control.Commit
import nl.roelofruis.artamus.core.api.Record.RecordNote
import nl.roelofruis.artamus.core.model.primitives.{Loudness, MidiNoteNumber}
import nl.roelofruis.artamus.core.model.record.{MillisecondPosition, RawMidiNote}
import javax.inject.Inject
import javax.sound.midi.{MidiMessage, Receiver, ShortMessage}
import client.midi.read.Midi

class MidiRecorder @Inject() (
  client: Client
) extends Thread with Receiver with LazyLogging {

  import _root_.client.infra.ClientInteraction._

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
              client.sendCommandList(List(RecordNote(note), Commit())) match {
                case None =>
                case Some((ex, _)) => logger.error("Unable to send recorded note", ex)
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
