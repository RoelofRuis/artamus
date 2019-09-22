package music.symbolic.pitched

trait TuningSystem[A] {
  val pcSeq: Seq[Int]

  val numSteps: Int = pcSeq.size
  val numPitchClasses: Int = pcSeq.last + 1

  def step(i: Int): Step = Step(i % numSteps)
  def pc(i: Int): PitchClass = PitchClass(i % numPitchClasses)
  def pcs: Seq[PitchClass] = Range(0, numPitchClasses).map(PitchClass)

  def pc2step(pc: PitchClass): Option[Step] = pcSeq.find(_ == pc.value).map(Step)
  def step2pc(step: Step): PitchClass = PitchClass(pcSeq(step.value))
  def pcDiff(pc1: PitchClass, pc2: PitchClass): Int = {
    val diff = pc2.value - pc1.value
    if (diff < 0) diff + numPitchClasses
    else diff
  }

  def interval2pc(in: Interval): PitchClass = {
    val stepPc = step2pc(in.s)
    val givenPc = in.pc
    val diff = stepPc.value - givenPc.value
    PitchClass(stepPc.value - diff)
  }

  def noteNumberToPitch(noteNumber: MidiNoteNumber): Pitch[PitchClass] = {
    Pitch(Octave((noteNumber.value / numPitchClasses) - 1), pc(noteNumber.value))
  }

  def pitchToNoteNumber(pitch: Pitch[PitchClass]): MidiNoteNumber = {
    MidiNoteNumber(((pitch.octave.value + 1) * numPitchClasses) + pitch.p.value)
  }
}
