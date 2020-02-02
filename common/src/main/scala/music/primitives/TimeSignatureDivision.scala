package music.primitives

import math.Rational
import math.temporal.Duration

final case class TimeSignatureDivision private (num: Int, denom: Int) {

  def beatDuration: Duration = Duration(Rational.reciprocal(denom))
  def barDuration: Duration = beatDuration * num

}

object TimeSignatureDivision {

  def apply(num: Int, denom: Int): Option[TimeSignatureDivision] = {
    if (num > 0 && math.isPowerOfTwo(denom)) Some(new TimeSignatureDivision(num, denom))
    else None
  }

}
