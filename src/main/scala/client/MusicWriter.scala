package client

import music.{Duration, Key, TimeSignature}
import protocol.ClientInterface
import server.api.Track.{AddNote, SetKey, SetTimeSignature}

/*
 * For now just an outline of what the higher level layer could look like
 */
class MusicWriter(client: ClientInterface) {

  def writeTimeSignature(t: TimeSignature): Boolean = client.sendCommand(SetTimeSignature(t)).getOrElse(false)

  def writeQuarterNote(midiPitch: Int): Boolean = client.sendCommand(AddNote(Duration.QUARTER_NOTE * 0, Duration.QUARTER_NOTE, midiPitch)).getOrElse(false)

  def writeKey(key: Key): Boolean = client.sendCommand(SetKey(key)).getOrElse(false)

}
