package music.analysis

import music.analysis.Analysis.{Chords, Functions, Intervals}
import music.domain.primitives._

object TwelveToneTuning {

  implicit val tuning: TuningSystem = TuningSystem(Seq(0, 2, 4, 5, 7, 9, 11))

  implicit object TwelveToneIntervals extends Intervals {
    val PERFECT_PRIME = Interval(PitchClass(0), Step(0))
    val SMALL_SECOND = Interval(PitchClass(1), Step(1))
    val LARGE_SECOND = Interval(PitchClass(2), Step(1))
    val SMALL_THIRD = Interval(PitchClass(3), Step(2))
    val LARGE_THIRD = Interval(PitchClass(4), Step(2))
    val PERFECT_FOURTH = Interval(PitchClass(5), Step(3))
    val AUGMENTED_FOURTH = Interval(PitchClass(6), Step(3))
    val DIMINISHED_FIFTH = Interval(PitchClass(6), Step(4))
    val PERFECT_FIFTH = Interval(PitchClass(7), Step(4))
    val SMALL_SIXTH = Interval(PitchClass(8), Step(5))
    val LARGE_SIXTH = Interval(PitchClass(9), Step(5))
    val DIMINISHED_SEVENTH = Interval(PitchClass(9), Step(6))
    val SMALL_SEVENTH = Interval(PitchClass(10), Step(6))
    val LARGE_SEVENTH = Interval(PitchClass(11), Step(6))

    override val intervals: Set[Interval] = Set(
      PERFECT_PRIME,
      SMALL_SECOND, LARGE_SECOND,
      SMALL_THIRD, LARGE_THIRD,
      PERFECT_FOURTH, AUGMENTED_FOURTH,
      DIMINISHED_FIFTH, PERFECT_FIFTH,
      SMALL_SIXTH, LARGE_SIXTH,
      DIMINISHED_SEVENTH, SMALL_SEVENTH, LARGE_SEVENTH
    )
  }

  implicit object TwelveToneFunctions extends Functions {
    val ROOT = Function(PitchClass(0), Step(0))
    val TWO = Function(PitchClass(2), Step(1))
    val FLAT_THREE = Function(PitchClass(3), Step(2))
    val THREE = Function(PitchClass(4), Step(2))
    val FOUR = Function(PitchClass(5), Step(3))
    val FIVE = Function(PitchClass(7), Step(4))
    val SIX = Function(PitchClass(9), Step(5))
    val SEVEN = Function(PitchClass(11), Step(6))

    override val intervalFunctionMapping: PartialFunction[Interval, Set[Function]] = {
      case TwelveToneIntervals.PERFECT_PRIME => Set(ROOT)
      case TwelveToneIntervals.LARGE_SECOND => Set(TWO)
      case TwelveToneIntervals.SMALL_THIRD => Set(FLAT_THREE)
      case TwelveToneIntervals.LARGE_THIRD => Set(THREE)
      case TwelveToneIntervals.PERFECT_FOURTH => Set(FOUR)
      case TwelveToneIntervals.PERFECT_FIFTH => Set(FIVE)
      case TwelveToneIntervals.LARGE_SIXTH => Set(SIX)
      case TwelveToneIntervals.LARGE_SEVENTH => Set(SEVEN)
    }
  }

  implicit object TwelveToneChords extends Chords {
    import TwelveToneFunctions._
    override val functionChordMapping: Seq[(Set[Function], String)] = Seq(
      (Set(ROOT, THREE, FIVE), "Major"),
      (Set(ROOT, FLAT_THREE, FIVE), "Minor")
    )
  }

}
