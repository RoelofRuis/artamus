package music.symbolic.tuning
import music.symbolic.const.Scales
import music.symbolic.{Accidental, MusicVector, PitchClass, Step}

object TwelveToneEqualTemprament extends Tuning {

  private val scale = Scales.MAJOR

  override def numDistinctSteps: Int = scale.numberOfSteps

  override def numDistinctPitches: Int = scale.numberOfPitches

  override def pitchClassToStep(pc: PitchClass): Option[Step] = {
    def find(currentPc: Int, scaleSeq: Seq[Int]): Option[Int] = {
      val step = scaleSeq.scan(0)(_ + _).indexOf(currentPc)
      if (step == -1) None else Some(step)
    }

    if (pc.value >= 0) find(pc.value, scale.stepSizes).map(Step)
    else find(-pc.value, scale.stepSizes.reverse).map(steps => Step(numDistinctSteps - steps))
  }

  override def stepToPitchClass(step: Step): PitchClass = {
    def loop(curStep: Int, total: Int, scaleSeq: Seq[Int]): Int = {
      if (curStep <= numDistinctSteps) scaleSeq.slice(0, curStep).sum + total
      else loop(curStep - numDistinctSteps, scaleSeq.sum, scaleSeq)
    }

    if (step.value >= 0) PitchClass(loop(step.value, 0, scale.stepSizes))
    else PitchClass(-loop(Math.abs(step.value), 0, scale.stepSizes.reverse))
  }

  override def musicVectorToPitchClass(mvec: MusicVector): PitchClass = {
    // Fixme: This won't work for accidentals with values larger than `scale.numberOfSteps`
    val newPc = stepToPitchClass(mvec.step).value + mvec.acc.value
    if (newPc < 0) PitchClass(numDistinctSteps - newPc)
    else if (newPc > numDistinctPitches) PitchClass(newPc - numDistinctPitches)
    else PitchClass(newPc)
  }

  override def +(mv1: MusicVector, mv2: MusicVector): MusicVector = {
    val newStep = mv1.step + mv2.step
    val newPc = stepToPitchClass(newStep).value
    val actualPc = (stepToPitchClass(mv1.step).value + mv1.acc.value) +
      (stepToPitchClass(mv2.step).value + mv2.acc.value)

    MusicVector(newStep % scale.numberOfSteps, Accidental(actualPc - newPc))
  }

  override def -(mv1: MusicVector, mv2: MusicVector): MusicVector = {
    val newStep = mv1.step - mv2.step
    val newPc = stepToPitchClass(newStep).value
    val actualPc = (stepToPitchClass(mv1.step).value + mv1.acc.value) -
      (stepToPitchClass(mv2.step).value + mv2.acc.value)

    MusicVector(newStep % scale.numberOfSteps, Accidental(actualPc - newPc))
  }
}
