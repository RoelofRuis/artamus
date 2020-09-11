package nl.roelofruis.artamus.core.track.analysis

import nl.roelofruis.artamus.core.common.Maths._
import nl.roelofruis.artamus.core.track.Temporal.Metre
import nl.roelofruis.artamus.core.common.{Duration, Rational}

trait TemporalMaths {

  implicit class MetreOps(metre: Metre) {
    def divide(i: Int): Option[Duration] = {
      val dur = metre
        .pulseGroups
        .map(pulseGroup => Rational(1, 2**pulseGroup.baseDuration) * pulseGroup.numberOfBeats)
        .reduceOption[Rational] { case (a: Rational, b: Rational) => a + b }

      dur.map(_ / i).flatMap(r => if (r.d.isPowerOfTwo) Some(Duration(r)) else None)
    }

    lazy val duration: Duration = Duration(
      metre
        .pulseGroups
        .foldRight(Rational(0)) { case (pulseGroup, acc) =>
          acc + Rational(pulseGroup.numberOfBeats, 2**pulseGroup.baseDuration)
        }
    )
  }

}
