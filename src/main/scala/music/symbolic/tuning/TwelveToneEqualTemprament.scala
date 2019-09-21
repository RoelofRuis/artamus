package music.symbolic.tuning

import music.symbolic.Pitched._
import music.symbolic.tuning.TwelveToneEqualTemprament.Functions._

// TODO: split this into appropriate parts!
object TwelveToneEqualTemprament {

  private val pcSteps = Seq(0, 2, 4, 5, 7, 9, 11)
  private val numPitchClasses = 12
  private val numSteps = pcSteps.size

  object Functions {
    val ROOT = Function(pc(0), step(0))
    val TWO = Function(pc(2), step(1))
    val THREE = Function(pc(4), step(2))
    val FOUR = Function(pc(5), step(3))
    val FIVE = Function(pc(7), step(4))
    val SIX = Function(pc(9), step(5))
    val SEVEN = Function(pc(11), step(6))
  }

  object Intervals {
    val PERFECT_PRIME = Interval(pc(0), step(0))
    val SMALL_SECOND = Interval(pc(1), step(1))
    val LARGE_SECOND = Interval(pc(2), step(1))
    val SMALL_THIRD = Interval(pc(3), step(2))
    val LARGE_THIRD = Interval(pc(4), step(2))
    val PERFECT_FOURTH = Interval(pc(5), step(3))
    val AUGMENTED_FOURTH = Interval(pc(6), step(3))
    val DIMINISHED_FIFTH = Interval(pc(6), step(4))
    val PERFECT_FIFTH = Interval(pc(7), step(4))
    val SMALL_SIXTH = Interval(pc(8), step(5))
    val LARGE_SIXTH = Interval(pc(9), step(5))
    val DIMINISHED_SEVENTH = Interval(pc(9), step(6))
    val SMALL_SEVENTH = Interval(pc(10), step(6))
    val LARGE_SEVENTH = Interval(pc(11), step(6))

    val ALL: Seq[Interval] = Seq(
      PERFECT_PRIME,
      SMALL_SECOND, LARGE_SECOND,
      SMALL_THIRD, LARGE_THIRD,
      PERFECT_FOURTH, AUGMENTED_FOURTH,
      DIMINISHED_FIFTH, PERFECT_FIFTH,
      SMALL_SIXTH, LARGE_SIXTH,
      DIMINISHED_SEVENTH, SMALL_SEVENTH, LARGE_SEVENTH
    )
  }

  // Creation
  def step(i: Int): Step = Step(i % numSteps)
  def pc(i: Int): PitchClass = PitchClass(i % numPitchClasses)

  def pitchClasses: Seq[PitchClass] = Range(0, numPitchClasses).map(PitchClass)

  def noteNumberToPitch(noteNumber: MidiNoteNumber): Pitch[PitchClass] = {
    Pitch(Octave((noteNumber.value / numPitchClasses) - 1), pc(noteNumber.value))
  }

  def pitchToNoteNumber(pitch: Pitch[PitchClass]): MidiNoteNumber = {
    MidiNoteNumber(((pitch.octave.value + 1) * 12) + pitch.p.value)
  }

  def pcToStep(pc: PitchClass): Option[Step] = {
    pcSteps.find(_ == pc.value).map(Step)
  }

  // Conversion
  def stepToPc(step: Step): PitchClass = PitchClass(pcSteps(step.value))

  def intervalToPc(in: Interval): PitchClass = {
    val stepPc = stepToPc(in.s)
    val givenPc = in.pc
    val diff = stepPc.value - givenPc.value
    PitchClass(stepPc.value - diff)
  }

  // Interpretation
  def possibleIntervals(pc1: PitchClass, pc2: PitchClass): Seq[Interval] = {
    val diff = pcDiff(pc1, pc2)
    Intervals.ALL.filter(intervalToPc(_).value == diff)
  }

  def functions(i: Interval): Seq[Function] = i match {
    case Intervals.PERFECT_PRIME => Seq(ROOT)
    case Intervals.LARGE_THIRD => Seq(THREE)
    case Intervals.PERFECT_FIFTH => Seq(FIVE)
    case _ => Seq()
  }

  def chordMap: Seq[Function] => Option[String] = { functions: Seq[Function]=>
    functions.sorted match {
      case Seq(ROOT, THREE, FIVE) => Some("Major")
      case _ => None
    }
  }

  private def pcDiff(pc1: PitchClass, pc2: PitchClass): Int = {
    val diff = pc2.value - pc1.value
    if (diff < 0) diff + numPitchClasses
    else diff
  }

}
