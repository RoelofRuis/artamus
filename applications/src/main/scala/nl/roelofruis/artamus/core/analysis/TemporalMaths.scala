package nl.roelofruis.artamus.core.analysis

import nl.roelofruis.artamus.core.Maths._
import nl.roelofruis.artamus.core.Temporal.Metre
import nl.roelofruis.artamus.core.primitives.{Duration, Rational}

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
