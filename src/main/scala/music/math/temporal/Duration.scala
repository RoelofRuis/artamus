package music.math.temporal

import music.math.Rational

/**
  * A non-negative duration expressed by a [[Rational]] value.
  *
  * @param v The duration value
  */
final case class Duration private (v: Rational) extends Comparable[Duration] {
  override def compareTo(o: Duration): Int = v compare o.v
  def *(i: Int): Duration = Duration(v * i)
}

object Duration {

  def apply(r: Rational): Duration = {
    if (r < Rational(0)) Duration.ZERO
    else new Duration(r)
  }

  lazy val ZERO: Duration = Duration(Rational(0))

}