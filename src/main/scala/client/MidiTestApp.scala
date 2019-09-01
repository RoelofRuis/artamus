package client

import client.midi.{BufferedMidiMessageReceiver, MyDevices}
import javax.sound.midi.MidiDevice

object MidiTestApp extends App {

  val device: MidiDevice = midi.loadDevice(MyDevices.iRigUSBMIDI_IN).get

  val transmitter = device.getTransmitter
  val receiver = new BufferedMidiMessageReceiver

  transmitter.setReceiver(receiver)

  device.open()
  receiver.startListening()

  println("Listening, press any key")
  System.in.read()

  println(receiver.stopListening())

  device.close()


}

