package music.primitives

import music.math.Rational

final case class Duration private (value: Rational) extends Comparable[Duration] {

  override def compareTo(o: Duration): Int = value compare o.value

  def isNone: Boolean = this == Duration.NONE

}

object Duration {

  def apply(r: Rational): Duration = {
    if (r < Rational(0)) NONE
    else new Duration(r)
  }

  lazy val NONE: Duration = Duration(Rational(0))

  lazy val QUARTER = Duration(Rational(1, 4))
  lazy val WHOLE = Duration(Rational(1))

}