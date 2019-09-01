package client

import music.Duration
import protocol.ClientInterface
import server.api.Track.{AddNote, SetKey, SetTimeSignature}

/*
 * For now just an outline of what the higher level layer could look like
 */
class MusicWriter(client: ClientInterface) {

  def writeTimeSignature(num: Int, denom: Int): Boolean = client.sendCommand(SetTimeSignature(num, denom)).getOrElse(false)

  def writeQuarterNote(midiPitch: Int): Boolean = client.sendCommand(AddNote(Duration.QUARTER_NOTE * 0, Duration.QUARTER_NOTE, midiPitch)).getOrElse(false)

  def writeKey(key: Int): Boolean = client.sendCommand(SetKey(key)).getOrElse(false)

}
