package artamus.core.model.primitives

import artamus.core.model.track.analysis.TuningBase

trait PitchClass {
  val value: Int

  def toStep(implicit tuning: TuningBase): Option[Step] = {
    tuning.pcSeq.indexOf(value) match {
      case i if i >= 0 => Some(Step(i))
      case _ => None
    }
  }

  def diff(other: PitchClass)(implicit tuning: TuningBase): Int = {
    val diff = other.value - value
    if (diff < 0) diff + tuning.numPitchClasses
    else diff
  }
}

object PitchClass {

  def apply(i: Int)(implicit tuning: TuningBase): PitchClass = PitchClassImpl(i % tuning.numPitchClasses)

  def listAll(implicit tuning: TuningBase): Seq[PitchClass] = Range(0, tuning.numPitchClasses).map(PitchClass.apply(_))

  private final case class PitchClassImpl(value: Int) extends PitchClass

}