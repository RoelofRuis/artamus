package client

import client.midi.{MidiDevices, MyDevices, TransmittingDevice}
import protocol.ClientInterface
import protocol.ClientInterface.EventListener
import server.api.Server.Disconnect
import server.api.Track._

object ClientApp extends App {

  val device = MidiDevices.loadDevice(MyDevices.FocusriteUSBMIDI_OUT).get
  val transmittingDevice = new TransmittingDevice(device)

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
  device.close()

}

class MusicWriter(client: ClientInterface) {

  def writeTimeSignature(num: Int, denom: Int): Boolean = client.sendCommand(SetTimeSignature(num, denom)).getOrElse(false)

  def writeQuarterNote(midiPitch: Int): Boolean = client.sendCommand(AddQuarterNote(midiPitch)).getOrElse(false)

  def writeKey(key: Int): Boolean = client.sendCommand(SetKey(key)).getOrElse(false)

}