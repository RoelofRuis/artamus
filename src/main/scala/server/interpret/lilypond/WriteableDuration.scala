package server.interpret.lilypond

import music.math.Rational
import music.primitives.Duration
import music.math._

final case class WriteableDuration(base: Rational, dots: Int)

object WriteableDuration {

  def from(duration: Duration): Seq[WriteableDuration] = {
    // TODO: complete missing implementations and implement as algorithm instead of cases!
    if ( ! isPowerOfTwo(duration.value.d)) throw new NotImplementedError(s"No WritableDuration support for [$duration]")
    else {
      duration.value.n match {
        case 1 => Seq(
          WriteableDuration(duration.value, 0)
        )

        case 3 => Seq(
          WriteableDuration(Rational.reciprocal(duration.value.d / 2), 1)
        )

        case 5 => Seq(
          WriteableDuration(Rational.reciprocal(duration.value.d / 4), 0),
          WriteableDuration(Rational.reciprocal(duration.value.d), 0)
        )

        case 7 => Seq(
          WriteableDuration(Rational.reciprocal(duration.value.d / 4), 2)
        )

        case _ => throw new NotImplementedError(s"No WriteableDuration support for [$duration]")
      }
    }
  }

}