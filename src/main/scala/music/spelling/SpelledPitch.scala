package music.spelling

import music.analysis.TuningSystem
import music.primitives.{Accidental, PitchClass, Step}

final case class SpelledPitch(step: Step, accidental: Accidental) {

  def toPc(implicit tuning: TuningSystem): PitchClass = PitchClass(tuning.pcSeq(step.value) + accidental.value)

}

// TODO: see whether this is still required
//def spellInterval(root: SpelledPitch, interval: Interval): SpelledPitch = {
//  def unboundStepValue(step: Int): Int = {
//    if (step >= numSteps) unboundStepValue(step - numSteps) + numPitchClasses
//    else pcSeqOld(step)
//  }
//
//  val newStep = root.step.value + interval.step.value
//  val newPc = unboundStepValue(newStep)
//  val actualPc = unboundStepValue(root.step.value) + root.accidental.value + interval.pc.value
//  val pcDiff = actualPc - newPc
//
//  SpelledPitch(Step(newStep), Accidental(pcDiff))
//}