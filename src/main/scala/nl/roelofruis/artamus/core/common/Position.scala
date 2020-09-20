package nl.roelofruis.artamus.core.common

/** A rational valued position. Its value might be negative.
  *
  * @param v The position value
  */
final case class Position(v: Rational) extends Ordered[Position] {

  override def compare(that: Position): Int = v compare that.v

  def -(that: Position): Duration = Duration(v - that.v)
  def -(that: Duration): Position = Position(v - that.v)
  def +(that: Duration): Position = Position(v + that.v)
  def *(i: Int): Position = Position(v * i)

}

object Position {

  lazy val ZERO: Position = Position(Rational(0))

  def apply(i: Int): Position = Position(Rational(i))
  def apply(n: Int, d: Int): Position = Position(Rational(n, d))

}