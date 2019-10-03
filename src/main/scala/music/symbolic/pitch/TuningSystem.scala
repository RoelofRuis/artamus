package music.symbolic.pitch

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

  def noteNumberToPitch(noteNumber: MidiNoteNumber): Pitch = {
    Pitch(Octave((noteNumber.value / numPitchClasses) - 1), pc(noteNumber.value))
  }

  def pitchToNoteNumber(pitch: Pitch): MidiNoteNumber = {
    MidiNoteNumber(((pitch.octave.value + 1) * numPitchClasses) + pitch.pitchClass.value)
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
