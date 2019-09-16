package music.symbolic

import music.math.Rational

final case class Duration(value: Rational) extends Comparable[Duration] {

  def dotted: Duration = Duration(value * Rational(3, 2))

  override def compareTo(o: Duration): Int = value compare o.value
}
