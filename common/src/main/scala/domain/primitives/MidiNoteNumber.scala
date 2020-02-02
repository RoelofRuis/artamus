package domain.primitives

import domain.write.analysis.TuningSystem

final case class MidiNoteNumber(value: Int) {

  def toPc(implicit tuning: TuningSystem): PitchClass = PitchClass(value)

  def toOct(implicit tuning: TuningSystem): Octave = Octave((value / tuning.numPitchClasses) - 1)

}

object MidiNoteNumber {

  def apply(oct: Octave, pc: PitchClass)(implicit tuning: TuningSystem): MidiNoteNumber = {
    MidiNoteNumber(((oct.value + 1) * tuning.numPitchClasses) + pc.value)
  }

}
