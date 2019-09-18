package music.interpret.harmony

import music.interpret.Interpretation
import music.interpret.Interpretation.{none, oneOf, only}
import music.symbolic.const.{IntervalFunctions, Intervals}
import music.symbolic.{Interval, IntervalFunction}

object JazzHarmony {

  def intervalToFunctions(i: Interval): Interpretation[IntervalFunction] = {
    i match {
      case Intervals.ROOT => only(IntervalFunctions.ROOT)
      case Intervals.FLAT_TWO => only(IntervalFunctions.FLAT_NINE)
      case Intervals.TWO => oneOf(IntervalFunctions.TWO, IntervalFunctions.NINE)
      case Intervals.FLAT_THREE => oneOf(IntervalFunctions.FLAT_THREE, IntervalFunctions.FLAT_TEN)
      case Intervals.THREE => only(IntervalFunctions.THREE)
      case Intervals.FOUR => oneOf(IntervalFunctions.FOUR, IntervalFunctions.ELEVEN)
      case Intervals.SHARP_FOUR => only(IntervalFunctions.SHARP_ELEVEN)
      case Intervals.FLAT_FIVE => only(IntervalFunctions.FLAT_FIVE)
      case Intervals.FIVE => only(IntervalFunctions.FIVE)
      case Intervals.SHARP_FIVE => only(IntervalFunctions.SHARP_FIVE)
      case Intervals.FLAT_SIX => only(IntervalFunctions.FLAT_THIRTEEN)
      case Intervals.SIX => oneOf(IntervalFunctions.SIX, IntervalFunctions.THIRTEEN)
      case Intervals.DIM_SEVEN => only(IntervalFunctions.DIM_SEVEN)
      case Intervals.FLAT_SEVEN => only(IntervalFunctions.FLAT_SEVEN)
      case Intervals.SEVEN => only(IntervalFunctions.SEVEN)
      case _ => none
    }
  }

}
