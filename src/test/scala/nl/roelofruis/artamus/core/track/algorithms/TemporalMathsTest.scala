package nl.roelofruis.artamus.core.track.algorithms

import nl.roelofruis.artamus.core.track.Temporal.{Metre, PulseGroup}
import nl.roelofruis.artamus.core.common.{Duration, Rational}
import utest._

object TemporalMathsTest extends TestSuite with TemporalMaths {

  // 4/4 in two major groups
  val metre1: Metre = Metre(Seq(PulseGroup(2, 2), PulseGroup(2, 2)))

  // 4/4 without splits
  val metre2: Metre = Metre(Seq(PulseGroup(2, 4)))

  val tests: Tests = Tests {
    test("divide the metre if possible") {
      assert(
        metre1.divide(1).contains(Duration(Rational(1, 1))),
        metre2.divide(1).contains(Duration(Rational(1, 1))),
        metre1.divide(2).contains(Duration(Rational(1, 2))),
        metre2.divide(2).contains(Duration(Rational(1, 2))),
        metre1.divide(3).isEmpty,
        metre2.divide(3).isEmpty,
        metre1.divide(4).contains(Duration(Rational(1, 4))),
        metre2.divide(4).contains(Duration(Rational(1, 4))),
        metre1.divide(8).contains(Duration(Rational(1, 8))),
        metre2.divide(8).contains(Duration(Rational(1, 8))),
      )
    }
    test("detect multiple pulse groups") {
      assert(
        metre1.hasMultiplePulseGroups,
        ! metre2.hasMultiplePulseGroups,
      )
    }
    test("time signature fraction") {
      assert(
        metre1.timeSignatureFraction == (4, 2),
        metre2.timeSignatureFraction == (4, 2),
      )
    }
  }

}
