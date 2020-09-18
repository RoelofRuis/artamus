package nl.roelofruis.artamus.core.common

/** A rational-valued non-negative duration.
  *
  * @param v The duration value
  */
final case class Duration private (v: Rational) extends Ordered[Duration] {
  override def compare(o: Duration): Int = v compare o.v
  def *(i: Int): Duration = Duration(v * i)
  def +(that: Duration): Duration = Duration(v + that.v)
}

object Duration {

  def apply(r: Rational): Duration = {
    if (r < Rational(0)) Duration.ZERO
    else new Duration(r)
  }

  lazy val ZERO: Duration = Duration(Rational(0))

}