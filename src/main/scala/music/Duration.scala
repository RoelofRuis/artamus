package music

import util.math.Rational

case class Duration(value: Rational)

object Duration {

  val QUARTER_NOTE = Duration(Rational(1, 4))

}
