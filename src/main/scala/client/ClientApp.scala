package client

import client.midi.MyDevices
import client.midi.in.ReadMidiMessage
import javax.sound.midi.ShortMessage
import protocol.ClientInterface.EventListener
import server.api.Server.Disconnect
import server.api.Track._

object ClientApp extends App {

  val transmittingDevice = midi.loadPlaybackDevice(MyDevices.FocusriteUSBMIDI_OUT).get
  val midiReader = midi.loadReader(MyDevices.iRigUSBMIDI_IN).get

  protocol.createClient(9999).map { client =>

    val writer = new MusicWriter(client)

    client.subscribe(EventListener[TrackSymbolsUpdated.type](_ => println("TrackSymbols are updated")))

    writer.writeTimeSignature(4, 4)
    writer.writeKey(0)

    client.subscribe(EventListener[TrackSymbolsUpdated.type] { _ =>
      val notes = client.sendQuery(GetTrackMidiNotes)
      println(s"Received Track Notes [$notes]")
      notes.foreach { notes =>
        transmittingDevice.sendQuarterNotes(notes)
      }
    })

    midiReader.read(ReadMidiMessage.noteOn(4))
      .map { case msg: ShortMessage => msg.getData1 }
      .foreach { writer.writeQuarterNote }

    client.sendControl(Disconnect(true))

    client.closeConnection()

  }

  midiReader.close()
  transmittingDevice.close()

}

