package nl.roelofruis.artamus.core.model.primitives

import nl.roelofruis.math._
import nl.roelofruis.math.temporal.Duration

final case class Metre(
  pulseGroups: Seq[PulseGroup]
) {
  lazy val duration: Duration = Duration(
      pulseGroups.foldRight(Rational(0)) { case (pulseGroup, acc) =>
        acc + Rational(pulseGroup.numberOfBeats, 2**pulseGroup.baseDuration)
      }
    )

  // TODO: improve to deal with groups with different fractions
  def getTimeSignatureFraction: (Int, FractionalPowerOfTwo) = {
    val pulses = pulseGroups.map(_.numberOfBeats).sum
    val fraction = pulseGroups.map(_.baseDuration).head
    (pulses, fraction)
  }
}
