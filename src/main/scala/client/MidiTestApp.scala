package client

import client.midi.{BatchMidiMessageReader, MyDevices}
import javax.sound.midi.MidiDevice

object MidiTestApp extends App {

  val device: MidiDevice = midi.loadDevice(MyDevices.iRigUSBMIDI_IN).get

  val reader = new BatchMidiMessageReader(device)

  val messages = reader.readBatch { queue =>
    Range(0, 4).map(_ => queue.take())
  }

  messages.foreach(println)

  reader.close()

}


