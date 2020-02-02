package music.primitives

import music.model.write.analysis.TuningSystem

trait Step {
  val value: Int
  def toPc(implicit tuning: TuningSystem): PitchClass = PitchClass(tuning.pcSeq(value))
}

object Step {

  def apply(i: Int)(implicit tuning: TuningSystem): Step = StepImpl(i % tuning.numSteps)

  private final case class StepImpl(value: Int) extends Step

}