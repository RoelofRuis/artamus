package music

case class TimeSignature private(num: Int, denom: Int)

object TimeSignature {

  def apply(num: Int, denom: Int): Option[TimeSignature] = {
    if (util.math.isPowerOfTwo(denom)) Some(new TimeSignature(num, denom))
    else None
  }

  lazy val `4/4`: TimeSignature = TimeSignature.apply(4, 4).get

}
