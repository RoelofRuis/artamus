package nl.roelofruis.artamus.core.track.algorithms

import nl.roelofruis.artamus.core.common.Maths._
import nl.roelofruis.artamus.core.common.Temporal.ProvidesDuration
import nl.roelofruis.artamus.core.common.{Duration, Rational}
import nl.roelofruis.artamus.core.track.Temporal.Metre

trait TemporalMaths {

  implicit val metreHasDuration: ProvidesDuration[Metre] = (metre: Metre) => {
    Duration(
      metre
        .pulseGroups
        .foldRight(Rational(0)) { case (pulseGroup, acc) =>
          acc + Rational(pulseGroup.numberOfBeats, 2**pulseGroup.baseDuration)
        }
    )
  }

  implicit class MetreOps(metre: Metre) {
    def divide(i: Int): Option[Duration] = {
      val dur = metre
        .pulseGroups
        .map(pulseGroup => Rational(1, 2**pulseGroup.baseDuration) * pulseGroup.numberOfBeats)
        .reduceOption[Rational] { case (a: Rational, b: Rational) => a + b }

      dur.map(_ / i).flatMap(r => if (r.d.isPowerOfTwo) Some(Duration(r)) else None)
    }

    // TODO: improve to deal with groups with different fractions
    lazy val timeSignatureFraction: (Int, FractionalPowerOfTwo) = {
      val pulses = metre.pulseGroups.map(_.numberOfBeats).sum
      val fraction = metre.pulseGroups.map(_.baseDuration).head
      (pulses, fraction)
    }
  }

}
