package music.primitives

import music.analysis.TuningSystem

final case class PitchSpelling(step: Step, accidental: Accidental) {

  def toPc(implicit tuning: TuningSystem): PitchClass = PitchClass(span)

  def span(implicit tuning: TuningSystem): Int = step.toPc.value + accidental.value

  def addInterval(interval: Interval)(implicit tuning: TuningSystem): PitchSpelling = {
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
