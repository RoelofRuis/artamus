package client.midi.in

import client.midi.util.BlockingQueueReader.BlockingQueueReadMethod
import client.midi.util.{BlockingQueueReader, TimedBlockingQueueReader}
import javax.sound.midi.{MidiMessage, Transmitter}

import scala.language.higherKinds

class MidiMessageReader (transmitter: Transmitter) extends BlockingQueueReader[MidiMessage] {

  private val reader = new TimedBlockingQueueReader[MidiMessage]
  private val receiver = new QueuedReceiver(reader)

  transmitter.setReceiver(receiver)

  def read[L[_]](readMethod: BlockingQueueReadMethod[MidiMessage, L]): L[MidiMessage] = reader.read(readMethod)

}
