package nl.roelofruis.artamus.core.track.algorithms

import nl.roelofruis.artamus.core.track.Temporal.{Metre, PulseGroup}
import nl.roelofruis.artamus.core.common.{Duration, Rational}
import utest._

object TemporalMathsTest extends TestSuite with TemporalMaths {

  val metre: Metre = Metre(Seq(PulseGroup(2, 4))) // represents 4/4th metre

  val tests: Tests = Tests {
    test("divide the metre if possible") {
      assert(
        metre.divide(1).contains(Duration(Rational(1, 1))),
        metre.divide(2).contains(Duration(Rational(1, 2))),
        metre.divide(3).isEmpty,
        metre.divide(4).contains(Duration(Rational(1, 4))),
        metre.divide(8).contains(Duration(Rational(1, 8)))
      )
    }
  }

}
