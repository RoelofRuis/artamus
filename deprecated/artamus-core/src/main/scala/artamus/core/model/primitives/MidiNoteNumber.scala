package artamus.core.model.primitives

import artamus.core.model.track.analysis.TuningBase

final case class MidiNoteNumber(value: Int) {

  def toPc(implicit tuning: TuningBase): PitchClass = PitchClass(value)

  def toOct(implicit tuning: TuningBase): Octave = Octave((value / tuning.numPitchClasses) - 1)

}

object MidiNoteNumber {

  def apply(oct: Octave, pc: PitchClass)(implicit tuning: TuningBase): MidiNoteNumber = {
    MidiNoteNumber(((oct.value + 1) * tuning.numPitchClasses) + pc.value)
  }

}
