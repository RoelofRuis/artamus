package music.domain.primitives

import music.analysis.TuningSystem

final class Step private (val value: Int) extends Serializable {

  def toPc(implicit tuning: TuningSystem): PitchClass = PitchClass(tuning.pcSeq(value))

}

object Step {

  def apply(i: Int)(implicit tuning: TuningSystem): Step = {
    new Step(i % tuning.numSteps)
  }

}