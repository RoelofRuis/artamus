package music.primitives

import music.math.Rational

final case class Duration private (value: Rational) extends Comparable[Duration] {

  override def compareTo(o: Duration): Int = value compare o.value

}

object Duration {

  def apply(r: Rational): Duration = {
    if (r < Rational(0)) ZERO
    else new Duration(r)
  }

  lazy val ZERO: Duration = Duration(Rational(0))

}