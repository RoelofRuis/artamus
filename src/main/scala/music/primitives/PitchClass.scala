package music.primitives

import music.analysis.TuningSystem

final class PitchClass private (val value: Int) extends Serializable {

  def toStep(implicit tuning: TuningSystem): Option[Step] = {
    tuning.pcSeq.indexOf(value) match {
      case i if i >= 0 => Some(Step(i))
      case _ => None
    }
  }

  def diff(other: PitchClass)(implicit tuning: TuningSystem): Int = {
    val diff = other.value - value
    if (diff < 0) diff + tuning.numPitchClasses
    else diff
  }

}

object PitchClass {

  def apply(i: Int)(implicit tuning: TuningSystem): PitchClass = {
    new PitchClass(i % tuning.numPitchClasses)
  }

  def listAll(implicit tuning: TuningSystem): Seq[PitchClass] =
    Range(0, tuning.numPitchClasses).map(PitchClass.apply(_))

}