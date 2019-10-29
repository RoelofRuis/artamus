package music.primitives

import music.math.Rational

final case class Position(value: Rational) extends Ordered[Position] {

  override def compare(that: Position): Int = value compare that.value

  def -(that: Position): Duration = Duration(value - that.value)
  def -(that: Duration): Position = Position(value - that.value)
  def isNegative: Boolean = value < Rational(0)

}

object Position {

  lazy val zero: Position = Position(Rational(0))

  def apply(duration: Duration, offset: Int): Position = Position(duration.value * offset)

}