package music

import music.Scale.ScaleMath

trait Scale {

  val stepSizes: Seq[Int]

  def math: ScaleMath = new ScaleMath(this)

}

object Scale {

  case object MajorScale extends Scale {
    val stepSizes: Seq[Int] = Seq(2, 2, 1, 2, 2, 2, 1)
  }

  class ScaleMath(scale: Scale) {

    val numberOfSteps: Int = scale.stepSizes.length

    def pitchClassToStep(pc: PitchClass): Option[Step] = {
      def find(currentPc: Int, scaleSeq: Seq[Int]): Option[Int] = {
        val step = scaleSeq.scan(0)(_ + _).indexOf(currentPc)
        if (step == -1) None else Some(step)
      }

      if (pc.value >= 0) find(pc.value, scale.stepSizes).map(Step)
      else find(-pc.value, scale.stepSizes.reverse).map(steps => Step(numberOfSteps - steps))
    }

    def stepToPitchClass(step: Step): PitchClass = {
      def loop(curStep: Int, total: Int, scaleSeq: Seq[Int]): Int = {
        if (curStep <= numberOfSteps) scaleSeq.slice(0, curStep).sum + total
        else loop(curStep - numberOfSteps, scaleSeq.sum, scaleSeq)
      }

      if (step.value >= 0) PitchClass(loop(step.value, 0, scale.stepSizes))
      else PitchClass(-loop(Math.abs(step.value), 0, scale.stepSizes.reverse))
    }

    def musicVectorToPitchClass(mvec: MusicVector): PitchClass = {
      // Fixme: This won't work for accidentals with values larger than `scale.numberOfSteps`
      val newPc = stepToPitchClass(mvec.step).value + mvec.acc.value
      if (newPc < 0) PitchClass(numberOfSteps - newPc)
      else if (newPc > numberOfSteps) PitchClass(newPc - numberOfSteps)
      else PitchClass(newPc)
    }

  }

  object ScaleMath {
    def apply(scale: Scale): ScaleMath = new ScaleMath(scale)

  }

}
