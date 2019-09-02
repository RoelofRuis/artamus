package music

import util.math.Rational

final case class Position(rational: Rational)

object Position {

  def apply(duration: Duration, offset: Int): Position = Position(duration.value * offset)

}