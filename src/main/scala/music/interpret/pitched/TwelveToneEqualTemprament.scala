package music.interpret.pitched

import Analysis.{DefinedChords, DefinedFunctions, DefinedIntervals}
import music.symbolic.pitched.{Function, Interval, TuningSystem}

object TwelveToneEqualTemprament {

  implicit val tuning: TuningSystem[TwelveToneEqualTemprament.type] = TuningSystem(Seq(0, 2, 4, 5, 7, 9, 11))

  implicit object Intervals extends DefinedIntervals[TwelveToneEqualTemprament.type] {
    val PERFECT_PRIME = Interval(tuning.pc(0), tuning.step(0))
    val SMALL_SECOND = Interval(tuning.pc(1), tuning.step(1))
    val LARGE_SECOND = Interval(tuning.pc(2), tuning.step(1))
    val SMALL_THIRD = Interval(tuning.pc(3), tuning.step(2))
    val LARGE_THIRD = Interval(tuning.pc(4), tuning.step(2))
    val PERFECT_FOURTH = Interval(tuning.pc(5), tuning.step(3))
    val AUGMENTED_FOURTH = Interval(tuning.pc(6), tuning.step(3))
    val DIMINISHED_FIFTH = Interval(tuning.pc(6), tuning.step(4))
    val PERFECT_FIFTH = Interval(tuning.pc(7), tuning.step(4))
    val SMALL_SIXTH = Interval(tuning.pc(8), tuning.step(5))
    val LARGE_SIXTH = Interval(tuning.pc(9), tuning.step(5))
    val DIMINISHED_SEVENTH = Interval(tuning.pc(9), tuning.step(6))
    val SMALL_SEVENTH = Interval(tuning.pc(10), tuning.step(6))
    val LARGE_SEVENTH = Interval(tuning.pc(11), tuning.step(6))

    override val INTERVALS: Seq[Interval] = Seq(
      PERFECT_PRIME,
      SMALL_SECOND, LARGE_SECOND,
      SMALL_THIRD, LARGE_THIRD,
      PERFECT_FOURTH, AUGMENTED_FOURTH,
      DIMINISHED_FIFTH, PERFECT_FIFTH,
      SMALL_SIXTH, LARGE_SIXTH,
      DIMINISHED_SEVENTH, SMALL_SEVENTH, LARGE_SEVENTH
    )
  }

  implicit object Functions extends DefinedFunctions[TwelveToneEqualTemprament.type] {
    val ROOT = Function(tuning.pc(0), tuning.step(0))
    val TWO = Function(tuning.pc(2), tuning.step(1))
    val THREE = Function(tuning.pc(4), tuning.step(2))
    val FOUR = Function(tuning.pc(5), tuning.step(3))
    val FIVE = Function(tuning.pc(7), tuning.step(4))
    val SIX = Function(tuning.pc(9), tuning.step(5))
    val SEVEN = Function(tuning.pc(11), tuning.step(6))

    override val INTERVAL_FUNCTION_MAPPING: PartialFunction[Interval, Seq[Function]] = {
      case Intervals.PERFECT_PRIME => Seq(ROOT)
      case Intervals.LARGE_THIRD => Seq(THREE)
      case Intervals.PERFECT_FIFTH => Seq(FIVE)
    }
  }

  implicit object Chords extends DefinedChords[TwelveToneEqualTemprament.type] {
    import Functions._
    override val FUNCTION_CHORD_MAPPING: Seq[(Seq[Function], String)] = Seq(
      (Seq(ROOT, THREE, FIVE), "Major"),
    )
  }

}
