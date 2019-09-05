package client.components

import music._
import protocol.client.ClientInterface
import server.domain.track.{AddNote, SetKey, SetTimeSignature}

/*
 * For now just an outline of what the higher level layer could look like
 */
class MusicWriter(client: ClientInterface) {

  def writeTimeSignature(t: TimeSignature): Boolean = client.sendCommand(SetTimeSignature(t)).getOrElse(false)

  def writeQuarterNote(midiPitch: Int): Boolean = client.sendCommand(
    AddNote(
      Position.apply(Duration.QUARTER, 0),
      Note(Duration.QUARTER, MidiPitch.fromMidiPitchNumber(midiPitch))
    )).getOrElse(false)

  def writeKey(key: Key): Boolean = client.sendCommand(SetKey(key)).getOrElse(false)

}
