package music

import music.properties.Pitch.MidiPitchNumber

final case class MidiPitch private (pitchClass: PitchClass, octave: Octave) {
  def getMidiPitchNumber: MidiPitchNumber = ((octave.value + 1) * 12) + pitchClass.value
}

object MidiPitch {

  def apply(i: MidiPitchNumber): MidiPitch = MidiPitch(PitchClass(i % 12), Octave((i / 12) - 1))

}