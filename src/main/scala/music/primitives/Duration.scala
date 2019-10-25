package music.primitives

import music.math.Rational

final case class Duration private (value: Rational) extends Comparable[Duration] {

  override def compareTo(o: Duration): Int = value compare o.value

}

object Duration {

  def apply(r: Rational): Duration = {
    if (r < Rational(0)) zero
    else new Duration(r)
  }

  lazy val zero: Duration = Duration(Rational(0))

  lazy val QUARTER = Duration(Rational(1, 4))

}