package client.components

import music._
import protocol.client.MessageBus
import server.domain.track.{AddNote, SetKey, SetTimeSignature}

/*
 * For now just an outline of what the higher level layer could look like
 */
class MusicWriter(messageBus: MessageBus) {

  def writeTimeSignature(t: TimeSignature): Boolean = messageBus.sendCommand(SetTimeSignature(t)).getOrElse(false)

  def writeQuarterNote(midiPitch: Int): Boolean = messageBus.sendCommand(
    AddNote(
      Position.apply(Duration.QUARTER, 0),
      Note(Duration.QUARTER, MidiPitch.fromMidiPitchNumber(midiPitch))
    )).getOrElse(false)

  def writeKey(key: Key): Boolean = messageBus.sendCommand(SetKey(key)).getOrElse(false)

}
