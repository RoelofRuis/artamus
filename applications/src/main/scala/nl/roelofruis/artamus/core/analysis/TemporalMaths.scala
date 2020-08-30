package nl.roelofruis.artamus.core.analysis

import nl.roelofruis.artamus.core.Maths._
import nl.roelofruis.artamus.core.Temporal.Metre
import nl.roelofruis.artamus.core.primitives.{Duration, Rational}

trait TemporalMaths {

  implicit class MetreOps(metre: Metre) {
    def divide(i: Int): Option[Duration] = {
      val dur = metre.pulseGroups
        .map(group => Rational(1, 2**group.baseDuration) * group.numberOfBeats)
        .reduceOption[Rational] { case (a: Rational, b: Rational) => a + b }

      dur.map(_ / i).flatMap(r => if (r.d.isPowerOfTwo) Some(Duration(r)) else None)
    }
  }

}
