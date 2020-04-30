package domain.primitives

import domain.math._
import domain.math.temporal.Duration

final case class Metre(
  pulseGroups: Seq[PulseGroup]
) {
  lazy val duration: Duration = Duration(
      pulseGroups.foldRight(Rational(0)) { case (pulseGroup, acc) =>
        acc + Rational(pulseGroup.numberOfBeats, 2**pulseGroup.baseDuration)
      }
    )

  // TODO: we can calculate the time signature fraction from this!
}
