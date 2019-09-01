package client.midi

import java.util.concurrent.BlockingQueue

import client.temporal.TemporalReader
import javax.sound.midi.{MidiDevice, MidiMessage}

import scala.language.higherKinds

class BatchMidiMessageReader(device: MidiDevice) {

  private val transmitter = device.getTransmitter
  private val receiver = new BufferedMidiMessageReceiver
  private val reader = new TemporalReader[MidiMessage](receiver)

  transmitter.setReceiver(receiver)

  if (! device.isOpen) device.open()

  def readBatch[L[_]](readMethod: BlockingQueue[MidiMessage] => L[MidiMessage]): L[MidiMessage] = reader.read(readMethod)

  def close(): Unit = device.close()

}
