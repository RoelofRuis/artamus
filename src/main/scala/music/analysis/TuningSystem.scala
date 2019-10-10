package music.analysis

import music.primitives.{Octave, PitchClass, _}

final case class TuningSystem[A](pcSeq: Seq[Int]) {

  val numSteps: Int = pcSeq.size
  val numPitchClasses: Int = pcSeq.last + 1

  def step(i: Int): Step = Step(i % numSteps)
  def pc(i: Int): PitchClass = PitchClass(i % numPitchClasses)
  def pcs: Seq[PitchClass] = Range(0, numPitchClasses).map(PitchClass)

  def pc2step(pc: PitchClass): Option[Step] = {
    pcSeq.indexOf(pc.value) match {
      case i if i >= 0 => Some(Step(i))
      case _ => None
    }
  }
  def step2pc(step: Step): PitchClass = pc(pcSeq(step.value))
  def pcDiff(pc1: PitchClass, pc2: PitchClass): Int = {
    val diff = pc2.value - pc1.value
    if (diff < 0) diff + numPitchClasses
    else diff
  }

  def noteNumberToPc(noteNumber: MidiNoteNumber): PitchClass = noteNumberToOctAndPc(noteNumber)._2

  def noteNumberToOctAndPc(noteNumber: MidiNoteNumber): (Octave, PitchClass) = {
    (Octave((noteNumber.value / numPitchClasses) - 1), pc(noteNumber.value))
  }

  def octAndPcToNoteNumber(oct: Octave, pc: PitchClass): MidiNoteNumber = {
    MidiNoteNumber(((oct.value + 1) * numPitchClasses) + pc.value)
  }

  def spellInterval(root: SpelledPitch, interval: Interval): SpelledPitch = {
    def unboundStepValue(step: Int): Int = {
      if (step >= numSteps) unboundStepValue(step - numSteps) + numPitchClasses
      else pcSeq(step)
    }

    val newStep = root.step.value + interval.step.value
    val newPc = unboundStepValue(newStep)
    val actualPc = unboundStepValue(root.step.value) + root.accidental.value + interval.pc.value
    val pcDiff = actualPc - newPc

    SpelledPitch(step(newStep), Accidental(pcDiff))
  }

}
