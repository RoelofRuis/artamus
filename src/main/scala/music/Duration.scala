package music

import music.math.Rational

final case class Duration(value: Rational) {

  def dotted: Duration = Duration(value * Rational(3, 2))

}

object Duration {

  val QUARTER = Duration(Rational(1, 4))

}
