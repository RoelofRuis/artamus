package artamus.core.model.primitives

import artamus.core.model.track.analysis.TuningBase

final case class PitchSpelling(step: Step, accidental: Accidental) {

  def toPc(implicit tuning: TuningBase): PitchClass = PitchClass(span)

  def span(implicit tuning: TuningBase): Int = step.toPc.value + accidental.value

  def addInterval(interval: Interval)(implicit tuning: TuningBase): PitchSpelling = {
    def unboundStepValue(step: Int): Int = {
      if (step >= tuning.numSteps) unboundStepValue(step - tuning.numSteps) + tuning.numPitchClasses
      else tuning.pcSeq(step)
    }

    val newStep = step.value + interval.step.value
    val newPc = unboundStepValue(newStep)
    val actualPc = unboundStepValue(step.value) + accidental.value + interval.pc.value
    val pcDiff = actualPc - newPc

    PitchSpelling(Step(newStep), Accidental(pcDiff))
  }
}
