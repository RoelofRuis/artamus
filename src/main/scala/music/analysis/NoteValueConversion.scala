package music.analysis

import music.math.temporal.Duration
import music.math.{Rational, _}
import music.domain.primitives.NoteValue

object NoteValueConversion {

  // TODO: complete missing implementations and implement as algorithm instead of cases!
  def from(duration: Duration): Seq[NoteValue] = {
    duration.v match {
      case Duration.ZERO.`v` => Seq()
      case Rational(_, d) if ! isPowerOfTwo(d) => throw new NotImplementedError(s"No WritableDuration support for [$duration]")
      case r @ Rational(1, _) => Seq(NoteValue(r, 0))
      case Rational(3, d) => Seq(NoteValue(Rational.reciprocal(d / 2), 1))
      case Rational(5, d) => Seq(
        NoteValue(Rational.reciprocal(d / 4), 0),
        NoteValue(Rational.reciprocal(d), 0)
      )
      case Rational(7, d) => Seq(NoteValue(Rational.reciprocal(d / 4), 2))
      case _ => throw new NotImplementedError(s"No WriteableDuration support for [$duration]")
    }
  }

}