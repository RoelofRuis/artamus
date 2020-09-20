package artamus.core.math.temporal

import artamus.core.math.Rational

/** A rational-valued non-negative duration.
  *
  * @param v The duration value
  */
final case class Duration private (v: Rational) extends Ordered[Duration] {
  override def compare(o: Duration): Int = v compare o.v
  def *(i: Int): Duration = Duration(v * i)
}

object Duration {

  def apply(r: Rational): Duration = {
    if (r < Rational(0)) Duration.ZERO
    else new Duration(r)
  }

  lazy val ZERO: Duration = Duration(Rational(0))

}