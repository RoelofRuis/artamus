package music.symbolic.const

import music.symbolic.const.Accidentals._
import music.symbolic.{Accidental, Interval, MusicVector, Step}

object Intervals {

  private def build(step: Int, acc: Accidental): Interval = Interval(MusicVector(Step(step), acc))

  lazy val ROOT: Interval = build(0, NEUTRAL)
  lazy val FLAT_TWO: Interval = build(1, FLAT)
  lazy val TWO: Interval = build(1, NEUTRAL)
  lazy val FLAT_THREE: Interval = build(2, FLAT)
  lazy val THREE: Interval = build(2, NEUTRAL)
  lazy val FOUR: Interval = build(3, NEUTRAL)
  lazy val SHARP_FOUR: Interval = build(3, SHARP)
  lazy val FLAT_FIVE: Interval = build(4, FLAT)
  lazy val FIVE: Interval = build(4, NEUTRAL)
  lazy val SHARP_FIVE: Interval = build(4, SHARP)
  lazy val FLAT_SIX: Interval = build(5, FLAT)
  lazy val SIX: Interval = build(5, NEUTRAL)
  lazy val SHARP_SIX: Interval = build(5, SHARP)
  lazy val DIM_SEVEN: Interval = build(6, DOUBLE_FLAT)
  lazy val FLAT_SEVEN: Interval = build(6, FLAT)
  lazy val SEVEN: Interval = build(6, NEUTRAL)

  lazy val NAMED_FUNCTIONS: Seq[Interval] = Seq(
    ROOT,
    FLAT_TWO, TWO,
    FLAT_THREE, THREE,
    FOUR, SHARP_FOUR,
    FLAT_FIVE, FIVE, SHARP_FIVE,
    FLAT_SIX, SIX, SHARP_SIX,
    DIM_SEVEN, FLAT_SEVEN, SEVEN
  )

}
