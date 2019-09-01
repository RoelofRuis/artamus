package client

import client.midi.MyDevices
import protocol.ClientInterface.EventListener
import server.api.Server.Disconnect
import server.api.Track._

object ClientApp extends App {

  val transmittingDevice = midi.loadTransmitter(MyDevices.FocusriteUSBMIDI_OUT).get

  val client = protocol.createClient(9999)

  val writer = new MusicWriter(client)

  client.subscribe(EventListener[TrackSymbolsUpdated.type](_ => println("TrackSymbols are updated")))

  writer.writeTimeSignature(4, 4)
  writer.writeKey(0)
  writer.writeQuarterNote(64)

  client.subscribe(EventListener[TrackSymbolsUpdated.type] { _ =>
    val notes = client.sendQuery(GetTrackMidiNotes)
    println(s"Received Track Notes [$notes]")
    notes.foreach { notes =>
      transmittingDevice.sendQuarterNotes(notes)
    }
  })

  writer.writeQuarterNote(66)
  writer.writeQuarterNote(67)

  client.sendControl(Disconnect(true))

  client.closeConnection()

  transmittingDevice.close()

}

