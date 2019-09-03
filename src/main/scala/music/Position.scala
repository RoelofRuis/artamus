package music

import util.math.Rational

final case class Position(value: Rational) extends Comparable[Position] {

  override def compareTo(o: Position): Int = value.compare(o.value)

}

object Position {

  def apply(duration: Duration, offset: Int): Position = Position(duration.value * offset)

}