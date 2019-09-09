package music

import music.properties.Pitch.IsPitched

final case class Note[A : IsPitched](duration: Duration, pitch: A)

object Note {

  def apply(duration: Duration, midiPitch: MidiPitch): Note[MidiPitch] = Note(duration, midiPitch)

}