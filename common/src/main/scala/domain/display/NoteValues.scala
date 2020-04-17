package domain.display

import domain.math.Rational
import domain.math.temporal.Duration
import domain.primitives.NoteValue

object NoteValues {

  import domain.math.IntegerMath

  implicit class DurationSlicingOps(duration: Duration) {
    // TODO: complete missing implementations and implement as algorithm instead of cases!
    def asNoteValues: Seq[NoteValue] = {
      duration.v match {
        case Duration.ZERO.`v` => Seq()
        case Rational(_, d) if ! d.isPowerOfTwo => throw new NotImplementedError(s"No WritableDuration support for [$duration]")
        case Rational(1, d) => Seq(NoteValue(d.largestPowerOfTwo, 0))
        case Rational(3, d) => Seq(NoteValue(d.largestPowerOfTwo - 1, 1))
        case Rational(5, d) => Seq(
          NoteValue(d.largestPowerOfTwo - 2, 0),
          NoteValue(d.largestPowerOfTwo, 0)
        )
        case Rational(7, d) => Seq(NoteValue(d.largestPowerOfTwo - 2, 2))
        case _ => throw new NotImplementedError(s"No WriteableDuration support for [$duration]")
      }
    }
  }

}