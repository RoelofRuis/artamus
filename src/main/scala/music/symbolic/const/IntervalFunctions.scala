package music.symbolic.const

import music.symbolic.IntervalFunction
import music.symbolic.tuning.TwelveToneEqualTemprament

object IntervalFunctions {

  // By definition this makes these functions only express something in 12 tone equal temprament
  val tuning: TwelveToneEqualTemprament.type = TwelveToneEqualTemprament

  lazy val ROOT: IntervalFunction = IntervalFunction(Intervals.ROOT)
  lazy val TWO: IntervalFunction = IntervalFunction(Intervals.TWO)
  lazy val FLAT_THREE: IntervalFunction = IntervalFunction(Intervals.FLAT_THREE)
  lazy val THREE: IntervalFunction = IntervalFunction(Intervals.THREE)
  lazy val FOUR: IntervalFunction = IntervalFunction(Intervals.FOUR)
  lazy val FLAT_FIVE: IntervalFunction = IntervalFunction(Intervals.FLAT_FIVE)
  lazy val FIVE: IntervalFunction = IntervalFunction(Intervals.FIVE)
  lazy val SHARP_FIVE: IntervalFunction = IntervalFunction(Intervals.SHARP_FIVE)
  lazy val FLAT_SIX: IntervalFunction = IntervalFunction(Intervals.FLAT_SIX)
  lazy val SIX: IntervalFunction = IntervalFunction(Intervals.SIX)
  lazy val SHARP_SIX: IntervalFunction = IntervalFunction(Intervals.SHARP_SIX)
  lazy val DIM_SEVEN: IntervalFunction = IntervalFunction(Intervals.DIM_SEVEN)
  lazy val FLAT_SEVEN: IntervalFunction = IntervalFunction(Intervals.FLAT_SEVEN)
  lazy val SEVEN: IntervalFunction = IntervalFunction(Intervals.SEVEN)
  lazy val FLAT_NINE: IntervalFunction = IntervalFunction(tuning.addIntervals(Intervals.OCTAVE, Intervals.FLAT_TWO))
  lazy val NINE: IntervalFunction = IntervalFunction(tuning.addIntervals(Intervals.OCTAVE, Intervals.TWO))
  lazy val FLAT_TEN: IntervalFunction = IntervalFunction(tuning.addIntervals(Intervals.OCTAVE, Intervals.FLAT_THREE))
  lazy val ELEVEN: IntervalFunction = IntervalFunction(tuning.addIntervals(Intervals.OCTAVE, Intervals.FOUR))
  lazy val SHARP_ELEVEN: IntervalFunction = IntervalFunction(tuning.addIntervals(Intervals.OCTAVE, Intervals.SHARP_FOUR))
  lazy val FLAT_THIRTEEN: IntervalFunction = IntervalFunction(tuning.addIntervals(Intervals.OCTAVE, Intervals.FLAT_SIX))
  lazy val THIRTEEN: IntervalFunction = IntervalFunction(tuning.addIntervals(Intervals.OCTAVE, Intervals.SIX))

}
