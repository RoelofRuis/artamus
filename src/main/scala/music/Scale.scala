package music

final case class Scale(steps: Seq[Int]) {
  def reverse: Seq[Int] = steps.reverse
}

object Scale {

  implicit class ScaleMath(scale: Scale) {

    val numberOfSteps: Int = scale.steps.length

    def pitchClassToStep(pc: PitchClass): Option[Step] = {
      def find(currentPc: Int, scaleSeq: Seq[Int]): Option[Int] = {
        val step = scaleSeq.scan(0)(_ + _).indexOf(currentPc)
        if (step == -1) None else Some(step)
      }

      if (pc.value >= 0) find(pc.value, scale.steps).map(Step)
      else find(-pc.value, scale.reverse).map(steps => Step(numberOfSteps - steps))
    }

    def stepToPitchClass(step: Step): PitchClass = {
      def loop(curStep: Int, total: Int, scaleSeq: Seq[Int]): Int = {
        if (curStep <= numberOfSteps) scaleSeq.slice(0, curStep).sum + total
        else loop(curStep - numberOfSteps, scaleSeq.sum, scaleSeq)
      }

      if (step.value >= 0) PitchClass(loop(step.value, 0, scale.steps))
      else PitchClass(-loop(Math.abs(step.value), 0, scale.reverse))
    }

  }

}
