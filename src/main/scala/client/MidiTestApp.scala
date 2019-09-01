package client

import client.midi.MyDevices
import client.midi.in.ReadMidiMessage
import javax.sound.midi.ShortMessage

object MidiTestApp extends App {

  val reader = midi.loadReader(MyDevices.iRigUSBMIDI_IN).get

  reader
    .read(ReadMidiMessage.noteOn(4))
    .map { case msg: ShortMessage => msg.getData1 }
    .foreach(println)

  reader.close()

}


