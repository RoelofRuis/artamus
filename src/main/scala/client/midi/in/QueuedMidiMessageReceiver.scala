package client.midi.in

import client.midi.util.BlockingQueueReader.BlockingQueueReadMethod
import client.midi.util.TimedBlockingQueueReader
import javax.sound.midi.{MidiMessage, Receiver, Transmitter}

import scala.language.higherKinds

private[midi] class QueuedMidiMessageReceiver(transmitter: Transmitter) extends MidiMessageReader with Receiver {

  private val reader = new TimedBlockingQueueReader[MidiMessage]

  transmitter.setReceiver(this)

  def read[L[_]](readMethod: BlockingQueueReadMethod[MidiMessage, L]): L[MidiMessage] = reader.read(readMethod)

  override def send(message: MidiMessage, timeStamp: Long): Unit = reader.write(message)

  override def close(): Unit = ()

}
