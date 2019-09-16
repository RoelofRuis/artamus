package music.interpret

import music.symbolic.const.{IntervalFunctions, Intervals}
import music.symbolic.{Interval, IntervalFunction}

object JazzHarmony {

  def intervalToFunctions(i: Interval): Seq[IntervalFunction] = {
    i match {
      case Intervals.ROOT => Seq(IntervalFunctions.ROOT)
      case Intervals.FLAT_TWO => Seq(IntervalFunctions.FLAT_NINE)
      case Intervals.TWO => Seq(IntervalFunctions.TWO, IntervalFunctions.NINE)
      case Intervals.FLAT_THREE => Seq(IntervalFunctions.FLAT_THREE, IntervalFunctions.FLAT_TEN)
      case Intervals.THREE => Seq(IntervalFunctions.THREE)
      case Intervals.FOUR => Seq(IntervalFunctions.FOUR, IntervalFunctions.ELEVEN)
      case Intervals.SHARP_FOUR => Seq(IntervalFunctions.SHARP_ELEVEN)
      case Intervals.FLAT_FIVE => Seq(IntervalFunctions.FLAT_FIVE)
      case Intervals.FIVE => Seq(IntervalFunctions.FIVE)
      case Intervals.SHARP_FIVE => Seq(IntervalFunctions.SHARP_FIVE)
      case Intervals.FLAT_SIX => Seq(IntervalFunctions.FLAT_THIRTEEN)
      case Intervals.SIX => Seq(IntervalFunctions.SIX, IntervalFunctions.THIRTEEN)
      case Intervals.DIM_SEVEN => Seq(IntervalFunctions.DIM_SEVEN)
      case Intervals.FLAT_SEVEN => Seq(IntervalFunctions.FLAT_SEVEN)
      case Intervals.SEVEN => Seq(IntervalFunctions.SEVEN)
      case _ => Seq()
    }
  }

}
