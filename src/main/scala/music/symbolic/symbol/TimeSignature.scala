package music.symbolic.symbol

import music.math

final case class TimeSignature private (num: Int, denom: Int)

object TimeSignature {

  def apply(num: Int, denom: Int): Option[TimeSignature] = {
    if (num > 0 && math.isPowerOfTwo(denom)) Some(new TimeSignature(num, denom))
    else None
  }

}
