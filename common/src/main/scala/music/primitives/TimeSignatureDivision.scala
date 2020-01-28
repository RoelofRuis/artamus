package music.primitives

import music.math.Rational
import music.math.temporal.Duration

final case class TimeSignatureDivision private (num: Int, denom: Int) {

  def beatDuration: Duration = Duration(Rational.reciprocal(denom))
  def barDuration: Duration = beatDuration * num

}

object TimeSignatureDivision {

  def apply(num: Int, denom: Int): Option[TimeSignatureDivision] = {
    if (num > 0 && music.math.isPowerOfTwo(denom)) Some(new TimeSignatureDivision(num, denom))
    else None
  }

  lazy val `4/4`: TimeSignatureDivision = TimeSignatureDivision(4, 4).get

}
