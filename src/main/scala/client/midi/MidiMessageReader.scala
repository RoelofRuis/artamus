package client.midi

import java.util.concurrent.BlockingQueue

import client.util.BlockingReader
import javax.sound.midi.{MidiDevice, MidiMessage}

import scala.language.higherKinds

class MidiMessageReader(device: MidiDevice) {

  private val transmitter = device.getTransmitter
  private val reader = new BlockingReader[MidiMessage]
  private val receiver = new BufferedMidiMessageReceiver(reader)

  transmitter.setReceiver(receiver)

  if (! device.isOpen) device.open()

  def read[L[_]](readMethod: BlockingQueue[MidiMessage] => L[MidiMessage]): L[MidiMessage] = reader.read(readMethod)

  def close(): Unit = device.close()

}
