package client

import protocol.ClientInterface
import server.api.Track.{AddQuarterNote, SetKey, SetTimeSignature}

/*
 * For now just an outline of what the higher level layer could look like
 */
class MusicWriter(client: ClientInterface) {

  def writeTimeSignature(num: Int, denom: Int): Boolean = client.sendCommand(SetTimeSignature(num, denom)).getOrElse(false)

  def writeQuarterNote(midiPitch: Int): Boolean = client.sendCommand(AddQuarterNote(midiPitch)).getOrElse(false)

  def writeKey(key: Int): Boolean = client.sendCommand(SetKey(key)).getOrElse(false)

}
