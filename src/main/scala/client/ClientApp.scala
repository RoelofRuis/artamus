package client

import protocol.ClientEventRegistry.Callback
import server.api.Server.Disconnect
import server.api.Track.{AddQuarterNote, SetKey, SetTimeSignature, TrackSymbolsUpdated}

object ClientApp extends App {

  val c = protocol.client(9999)

  c.subscribeToEvent(Callback[TrackSymbolsUpdated.type](_ => println("Callback A")))

  c.sendCommand(SetTimeSignature(4, 4))
  c.sendCommand(SetKey(0))
  c.sendCommand(AddQuarterNote(64))

  c.subscribeToEvent(Callback[TrackSymbolsUpdated.type](_ => println("Callback B")))

  c.sendCommand(AddQuarterNote(66))

  c.sendControlMessage(Disconnect(true))

  c.close()

}


