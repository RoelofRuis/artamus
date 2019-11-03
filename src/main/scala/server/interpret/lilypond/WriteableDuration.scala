package server.interpret.lilypond

import music.math.Rational
import music.primitives.Duration
import music.math._

final case class WriteableDuration(base: Rational, dots: Int)

object WriteableDuration {

  def from(duration: Duration): Seq[WriteableDuration] = {
    // TODO: complete missing implementations and implement as algorithm instead of cases!
    duration.value match {
      case Duration.NONE.value => Seq()
      case Rational(_, d) if ! isPowerOfTwo(d) => throw new NotImplementedError(s"No WritableDuration support for [$duration]")
      case r @ Rational(1, _) => Seq(WriteableDuration(r, 0))
      case Rational(3, d) => Seq(WriteableDuration(Rational.reciprocal(d / 2), 1))
      case Rational(5, d) => Seq(
        WriteableDuration(Rational.reciprocal(d / 4), 0),
        WriteableDuration(Rational.reciprocal(d), 0)
      )
      case Rational(7, d) => Seq(WriteableDuration(Rational.reciprocal(d / 4), 2))
      case _ => throw new NotImplementedError(s"No WriteableDuration support for [$duration]")
    }
  }

}