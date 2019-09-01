package client

import client.midi.{BatchMidiMessageReader, MyDevices}
import javax.sound.midi.MidiDevice

object MidiTestApp extends App {

  val device: MidiDevice = midi.loadDevice(MyDevices.iRigUSBMIDI_IN).get

  val reader = new BatchMidiMessageReader(device)

  reader.startReading()
  println("Reading, press <enter>")
  System.in.read()

  val messages = reader.stopReading()

  println(messages)

  reader.close()




}

