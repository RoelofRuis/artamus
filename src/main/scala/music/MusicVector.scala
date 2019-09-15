package music

import music.math.ScaleMath

//TODO: figure out: is this the same as a spelled pitch?

/**
  * Data structure that is pointing (like a mathematical vector) to a note from a given scale step.
  *
  * In this way, a distinction can be made between for instance a sharp four and a flat five, while still resolving to
  * the same midi pitch (or piano key)
  *
  * Example (steps are zero-indexed):
  * Sharp Four: [[MusicVector(3, 1)]]
  * Flat Five: [[MusicVector(4, -1)]]
  */
final case class MusicVector private (step: Step, acc: Accidental) extends Comparable[MusicVector] {

  override def compareTo(other: MusicVector): Int = {
    val stepCompare = step.value.compare(other.step.value)
    if (stepCompare != 0) stepCompare
    else acc.value.compare(other.acc.value)
  }

  override def toString: String = s"MV[${step.value},${acc.value}]"

}

object MusicVector {

  lazy val zero = MusicVector(Step(0), Accidental(0))

  /**
    * Allows several mathematical operations on MusicVectors when the scale to be used is in scope
    * TODO: should this depend on ScaleMath or something else?
    */
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
