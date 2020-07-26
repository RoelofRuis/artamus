package artamus.core.model.primitives

import artamus.core.model.track.analysis.TuningBase

trait Step {
  val value: Int
  def toPc(implicit tuning: TuningBase): PitchClass = PitchClass(tuning.pcSeq(value))
}

object Step {

  def apply(i: Int)(implicit tuning: TuningBase): Step = StepImpl(i % tuning.numSteps)

  private final case class StepImpl(value: Int) extends Step

}