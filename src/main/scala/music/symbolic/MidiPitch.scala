package music.symbolic

final case class MidiPitch private (octave: Octave, pitchClass: PitchClass)

object MidiPitch {

  def apply(i: MidiNoteNumber): MidiPitch = MidiPitch(Octave(i), PitchClass(i))

}