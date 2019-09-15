package music.symbolic

import music.math.Rational

final case class Duration(value: Rational) {

  def dotted: Duration = Duration(value * Rational(3, 2))

}
