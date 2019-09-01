package client.midi

import javax.sound.midi.{MidiDevice, MidiMessage}

class BatchMidiMessageReader(device: MidiDevice) {

  private val transmitter = device.getTransmitter
  private val receiver = new BufferedMidiMessageReceiver

  transmitter.setReceiver(receiver)

  if (! device.isOpen) device.open()

  def startReading(): Unit = receiver.startReading()

  def stopReading(): List[MidiMessage] = receiver.stopReading()

  def close(): Unit = device.close()

}
