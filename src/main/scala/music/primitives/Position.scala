package music.primitives

import music.math.Rational

final case class Position(value: Rational) extends Comparable[Position] {

  override def compareTo(o: Position): Int = value compare o.value

}

object Position {

  lazy val zero: Position = Position(Rational(0))

  def apply(duration: Duration, offset: Int): Position = Position(duration.value * offset)

}