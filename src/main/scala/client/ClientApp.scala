package client

import protocol.EventListener
import server.api.Server.Disconnect
import server.api.Track.{AddQuarterNote, SetKey, SetTimeSignature, TrackSymbolsUpdated}

object ClientApp extends App {

  val c = protocol.client(9999)

  c.subscribe(EventListener[TrackSymbolsUpdated.type](_ => println("Callback A")))

  c.sendCommand(SetTimeSignature(4, 4))
  c.sendCommand(SetKey(0))
  c.sendCommand(AddQuarterNote(64))

  c.subscribe(EventListener[TrackSymbolsUpdated.type](_ => println("Callback B")))

  c.sendCommand(AddQuarterNote(66))

  c.sendControl(Disconnect(true))

  c.closeConnection()

}


