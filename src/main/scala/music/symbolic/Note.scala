package music.symbolic

import music.symbolic.properties.Pitch.HasExactPitch

final case class Note[A : HasExactPitch](duration: Duration, pitch: A)

object Note {

  def apply(duration: Duration, midiPitch: MidiPitch): Note[MidiPitch] = new Note(duration, midiPitch)

}