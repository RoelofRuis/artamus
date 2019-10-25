package music.primitives

import music.math.Rational

final case class Duration(value: Rational) extends Comparable[Duration] {
  override def compareTo(o: Duration): Int = value compare o.value
}

object Duration {

  lazy val zero: Duration = Duration(Rational(0))

  lazy val QUARTER = Duration(Rational(1, 4))

}