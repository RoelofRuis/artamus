package music

import music.properties.Pitch.MidiPitchNumber

final case class MidiPitch private (octave: Octave, pitchClass: PitchClass) {
  def getMidiPitchNumber: MidiPitchNumber = ((octave.value + 1) * 12) + pitchClass.value
}

object MidiPitch {

  def apply(i: MidiPitchNumber): MidiPitch = MidiPitch(Octave((i / 12) - 1), PitchClass(i % 12))

}