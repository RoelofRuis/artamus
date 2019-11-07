package music.primitives

import music.math
import music.math.Rational

final case class TimeSignatureDivision private (num: Int, denom: Int) {

  def beatDuration: Duration = Duration(Rational.reciprocal(denom))
  def barDuration: Duration = Duration(beatDuration.value * num)

}

object TimeSignatureDivision {

  def apply(num: Int, denom: Int): Option[TimeSignatureDivision] = {
    if (num > 0 && math.isPowerOfTwo(denom)) Some(new TimeSignatureDivision(num, denom))
    else None
  }

  lazy val `4/4`: TimeSignatureDivision = TimeSignatureDivision(4, 4).get

}
