package client

import client.midi.{MidiMessageReader, MyDevices}
import javax.sound.midi.MidiDevice

object MidiTestApp extends App {

  val device: MidiDevice = midi.loadDevice(MyDevices.iRigUSBMIDI_IN).get

  val reader = new MidiMessageReader(device)

  val messages = reader.read { queue =>
    Range(0, 4).map(_ => queue.take())
  }

  messages.foreach(println)

  reader.close()

}


