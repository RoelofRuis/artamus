package client

import protocol.EventListener
import server.api.Server.Disconnect
import server.api.Track._

object ClientApp extends App {

  val c = protocol.client(9999)

  c.subscribe(EventListener[TrackSymbolsUpdated.type](_ => println("Callback A")))

  c.sendCommand(SetTimeSignature(4, 4))
  c.sendCommand(SetKey(0))
  c.sendCommand(AddQuarterNote(64))

  c.subscribe(EventListener[TrackSymbolsUpdated.type](_ => println("Callback B")))

  c.sendCommand(AddQuarterNote(66))

  val queryRes = c.sendQuery(GetTrackMidiNotes)
  print(queryRes)

  c.sendControl(Disconnect(true))

  c.closeConnection()

}


