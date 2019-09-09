package music

import music.properties.Pitch.HasExactPitch

final case class Note[A : HasExactPitch](duration: Duration, pitch: A)

object Note {

  def apply(duration: Duration, midiPitch: MidiPitch): Note[MidiPitch] = Note(duration, midiPitch)

}