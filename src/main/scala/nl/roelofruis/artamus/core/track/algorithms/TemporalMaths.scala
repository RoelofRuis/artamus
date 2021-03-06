package nl.roelofruis.artamus.core.track.algorithms

import nl.roelofruis.artamus.core.common.Maths._
import nl.roelofruis.artamus.core.common.Temporal.ProvidesDuration
import nl.roelofruis.artamus.core.common.{Duration, Rational}
import nl.roelofruis.artamus.core.track.Temporal.{Metre, PulseGroup}

trait TemporalMaths {

  def makeDuration(power: FractionalPowerOfTwo, dots: Int): Duration = {
    Duration(Rational(1, 2**power) * (Rational(1) + Rational(2**dots-1, 2**dots)))
  }

  implicit val metreHasDuration: ProvidesDuration[Metre] = (metre: Metre) => {
    Duration(
      metre
        .pulseGroups
        .foldRight(Rational(0)) { case (pulseGroup, acc) =>
          acc + Rational(pulseGroup.numberOfBeats, 2**pulseGroup.baseDuration)
        }
    )
  }

  implicit class PulseGroupOps(pulseGroup: PulseGroup) {
    lazy val duration: Duration = Duration(Rational(1, 2**pulseGroup.baseDuration) * pulseGroup.numberOfBeats)
  }

  implicit class MetreOps(metre: Metre) {
    def divide(i: Int): Option[Duration] = {
      val dur = metre
        .pulseGroups
        .map(pulseGroup => Rational(1, 2**pulseGroup.baseDuration) * pulseGroup.numberOfBeats)
        .reduceOption[Rational] { case (a: Rational, b: Rational) => a + b }

      dur.map(_ / i).flatMap(r => if (r.d.isPowerOfTwo) Some(Duration(r)) else None)
    }

    def hasMultiplePulseGroups: Boolean = metre.pulseGroups.length > 1

    // TODO: improve to deal with groups with different fractions
    lazy val timeSignatureFraction: (Int, FractionalPowerOfTwo) = {
      val pulses = metre.pulseGroups.map(_.numberOfBeats).sum
      val fraction = metre.pulseGroups.map(_.baseDuration).head
      (pulses, fraction)
    }
  }

}
