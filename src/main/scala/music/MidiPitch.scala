package music

import music.MidiPitch.MidiPitchNumber

final case class MidiPitch private (pitchClass: PitchClass, octave: Octave) {
  def toMidiPitchNumber: MidiPitchNumber = ((octave.value + 1) * 12) + pitchClass.value
}

object MidiPitch {

  type MidiPitchNumber = Int

  def apply(i: MidiPitchNumber): MidiPitch = MidiPitch(PitchClass(i % 12), Octave((i / 12) - 1))

}