package music.symbolic.temporal

import music.math.Rational

final case class Duration(value: Rational) extends Comparable[Duration] {
  override def compareTo(o: Duration): Int = value compare o.value
}

object Duration {

  val QUARTER = Duration(Rational(1, 4))

}