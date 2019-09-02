package music

import music.Scale.ScaleMath

final case class MusicVector(step: Step, acc: Accidental) extends Comparable[MusicVector] {

  override def compareTo(other: MusicVector): Int = {
    val stepCompare = step.value.compare(other.step.value)
    if (stepCompare != 0) stepCompare
    else acc.value.compare(other.acc.value)
  }

  override def toString: String = s"MV[${step.value},${acc.value}]"

}

object MusicVector {

  implicit class MusicVectorMath(vector: MusicVector)(implicit scale: ScaleMath) {

    def +(other: MusicVector): MusicVector = {
      val newStep = vector.step + other.step
      val newPc = scale.stepToPitchClass(newStep).value
      val actualPc = (scale.stepToPitchClass(vector.step).value + vector.acc.value) +
        (scale.stepToPitchClass(other.step).value + other.acc.value)

      MusicVector(newStep % scale.numberOfSteps, Accidental(actualPc - newPc))
    }

    def -(other: MusicVector): MusicVector = {
      val newStep = vector.step - other.step
      val newPc = scale.stepToPitchClass(newStep).value
      val actualPc = (scale.stepToPitchClass(vector.step).value + vector.acc.value) -
        (scale.stepToPitchClass(other.step).value + other.acc.value)

      MusicVector(newStep % scale.numberOfSteps, Accidental(actualPc - newPc))
    }

  }

}
