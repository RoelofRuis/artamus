package client

import protocol.ClientInterface
import protocol.ClientInterface.EventListener
import server.api.Server.Disconnect
import server.api.Track._

object ClientApp extends App {

  val client = protocol.createClient(9999)

  val writer = new MusicWriter(client)

  client.subscribe(EventListener[TrackSymbolsUpdated.type](_ => println("TrackSymbols are updated")))

  writer.writeTimeSignature(4, 4)
  writer.writeKey(0)
  writer.writeQuarterNote(64)

  client.subscribe(EventListener[TrackSymbolsUpdated.type] { _ =>
    // val notes = client.sendQuery(GetTrackMidiNotes) TODO: Allow this call!
    // println(s"Notes are now [$notes]")
  })

  writer.writeQuarterNote(66)
  writer.writeQuarterNote(67)

  client.sendControl(Disconnect(true))

  client.closeConnection()

}

class MusicWriter(client: ClientInterface) {

  def writeTimeSignature(num: Int, denom: Int): Boolean = client.sendCommand(SetTimeSignature(num, denom)).getOrElse(false)

  def writeQuarterNote(midiPitch: Int): Boolean = client.sendCommand(AddQuarterNote(midiPitch)).getOrElse(false)

  def writeKey(key: Int): Boolean = client.sendCommand(SetKey(key)).getOrElse(false)

}