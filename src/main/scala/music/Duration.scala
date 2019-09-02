package music

import util.math.Rational

final case class Duration(value: Rational) {

  def dotted: Duration = Duration(value * Rational(3, 2))

}

object Duration {

  val QUARTER_NOTE = Duration(Rational(1, 4))

}
