package client.midi.in

import client.util.BlockingQueueReader.BlockingQueueReadMethod
import client.util.{BlockingQueueReadWrite, BlockingQueueReader}
import javax.sound.midi.{MidiDevice, MidiMessage}

import scala.language.higherKinds

class MidiMessageReader private[midi] (device: MidiDevice) extends BlockingQueueReader[MidiMessage] {

  private val transmitter = device.getTransmitter
  private val reader = new BlockingQueueReadWrite[MidiMessage]
  private val receiver = new BufferedMidiMessageReceiver(reader)

  transmitter.setReceiver(receiver)

  if (! device.isOpen) device.open()

  def read[L[_]](readMethod: BlockingQueueReadMethod[MidiMessage, L]): L[MidiMessage] = reader.read(readMethod)

}
