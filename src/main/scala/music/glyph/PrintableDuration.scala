package music.glyph

import music.math.{Rational, _}
import music.primitives.Duration

// TODO: where to fit this class? It is more like an algorithm than an actual `Printable` or glyph
final case class PrintableDuration(base: Rational, dots: Int)

object PrintableDuration {

  def from(duration: Duration): Seq[PrintableDuration] = {
    // TODO: complete missing implementations and implement as algorithm instead of cases!
    duration.value match {
      case Duration.NONE.value => Seq()
      case Rational(_, d) if ! isPowerOfTwo(d) => throw new NotImplementedError(s"No WritableDuration support for [$duration]")
      case r @ Rational(1, _) => Seq(PrintableDuration(r, 0))
      case Rational(3, d) => Seq(PrintableDuration(Rational.reciprocal(d / 2), 1))
      case Rational(5, d) => Seq(
        PrintableDuration(Rational.reciprocal(d / 4), 0),
        PrintableDuration(Rational.reciprocal(d), 0)
      )
      case Rational(7, d) => Seq(PrintableDuration(Rational.reciprocal(d / 4), 2))
      case _ => throw new NotImplementedError(s"No WriteableDuration support for [$duration]")
    }
  }

}