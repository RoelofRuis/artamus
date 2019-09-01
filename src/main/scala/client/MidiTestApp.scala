package client

import client.midi.{MidiMessageReader, MyDevices}
import client.util.Read
import javax.sound.midi.{MidiDevice, MidiMessage}

object MidiTestApp extends App {

  val device: MidiDevice = midi.loadDevice(MyDevices.iRigUSBMIDI_IN).get

  val reader = new MidiMessageReader(device)

  val messages: List[MidiMessage] = reader.read(Read.numElements(4))

  messages.foreach(println)

  reader.close()

}


