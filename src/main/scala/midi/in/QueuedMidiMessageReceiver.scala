package midi.in

import midi.util.TemporalReadableBlockingQueue.BlockingQueueReadMethod
import midi.util.TemporalReadableBlockingQueue
import javax.sound.midi.{MidiMessage, Receiver, Transmitter}

import scala.language.higherKinds

private[midi] class QueuedMidiMessageReceiver(transmitter: Transmitter) extends MidiMessageReader with Receiver {

  private val queue = new TemporalReadableBlockingQueue[MidiMessage]

  transmitter.setReceiver(this)

  def read[L[_]](readMethod: BlockingQueueReadMethod[MidiMessage, L]): L[MidiMessage] = queue.read(readMethod)

  override def send(message: MidiMessage, timeStamp: Long): Unit = queue.offer(message)

  override def close(): Unit = ()

}
