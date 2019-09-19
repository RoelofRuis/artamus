package music.symbolic.tuning
import music.symbolic._
import music.symbolic.const.Scales

// TODO: try to extract operations and essential constants when this class is more mature
object TwelveToneEqualTemprament extends Tuning {

  override def stepSizes: Seq[Int] = Scales.MAJOR.stepSizes
  override def numDistinctSteps: Int = Scales.MAJOR.numberOfSteps
  override def numDistinctPitches: Int = Scales.MAJOR.numberOfPitches

  override def pitchClassToStep(pc: PitchClass): Option[Step] = {
    def find(currentPc: Int, scaleSeq: Seq[Int]): Option[Int] = {
      val step = scaleSeq.scan(0)(_ + _).indexOf(currentPc)
      if (step == -1) None else Some(step)
    }

    if (pc.value >= 0) find(pc.value, stepSizes).map(Step)
    else find(-pc.value, stepSizes.reverse).map(steps => Step(numDistinctSteps - steps))
  }

  override def stepToPitchClass(step: Step): PitchClass = {
    def loop(curStep: Int, total: Int, scaleSeq: Seq[Int]): Int = {
      if (curStep <= numDistinctSteps) scaleSeq.slice(0, curStep).sum + total
      else loop(curStep - numDistinctSteps, scaleSeq.sum, scaleSeq)
    }

    if (step.value >= 0) PitchClass(loop(step.value, 0, stepSizes))
    else PitchClass(-loop(Math.abs(step.value), 0, stepSizes.reverse))
  }

  override def musicVectorToPitchClass(mvec: MusicVector): PitchClass = {
    // Fixme: This won't work for accidentals with values larger than `scale.numberOfSteps`
    val newPc = stepToPitchClass(mvec.step).value + mvec.acc.value
    if (newPc < 0) PitchClass(numDistinctSteps - newPc)
    else if (newPc > numDistinctPitches) PitchClass(newPc - numDistinctPitches)
    else PitchClass(newPc)
  }

  override def vector(step: Int, accidental: Int): MusicVector = {
    MusicVector(Step(step), Accidental(accidental))
  }

  override def rectify(mv: MusicVector): MusicVector = {
    if (mv.step.value >= 0) mv
    else {
      val rectifiedValue= mv.step.value + ((mv.step.value / -numDistinctSteps) + 1) * numDistinctSteps
      MusicVector(Step(rectifiedValue), mv.acc)
    }
  }

  override def subtract(mv1: MusicVector, mv2: MusicVector): MusicVector = {
    val newStep = mv1.step.value - mv2.step.value
    val newPc = stepToPitchClass(Step(newStep)).value
    val actualPc = musicVectorToPitchClass(mv1).value - musicVectorToPitchClass(mv2).value

    val pcDiff = actualPc - newPc

    MusicVector(Step(newStep % numDistinctSteps), Accidental(pcDiff))
  }

  override def compare(mvec: MusicVector, pc: PitchClass): Boolean = {
    musicVectorToPitchClass(mvec) == pc
  }

  override def addIntervals(i1: Interval, i2: Interval): Interval = {
    val newStep = i1.musicVector.step + i2.musicVector.step
    val newPc = stepToPitchClass(newStep).value
    val actualPc = musicVectorToPitchClass(i1.musicVector).value + musicVectorToPitchClass(i2.musicVector).value

    Interval(MusicVector(newStep, Accidental(actualPc - newPc)))
  }

  override def transpose(i1: Interval, i2: Interval): Interval = {
    Interval(transpose(i1.musicVector, i2))
  }

  override def transpose(mv: MusicVector, i: Interval): MusicVector = {
    val newStep = mv.step + i.musicVector.step
    val newPc = stepToPitchClass(newStep).value
    val actualPc = musicVectorToPitchClass(mv).value + musicVectorToPitchClass(i.musicVector).value

    MusicVector(newStep % numDistinctSteps, Accidental(actualPc - newPc))
  }

}
