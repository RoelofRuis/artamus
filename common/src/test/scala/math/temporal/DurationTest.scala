package math.temporal

import math.Rational
import utest._

object DurationTest extends TestSuite {

  val tests: Tests = Tests {
    test("create") {
      assert(
        Duration(Rational(1, 2)).v == Rational(1, 2)
      )
    }
    test("do not allow negative durations") {
      assert(
        Duration.ZERO.v == Rational(0),
        Duration(Rational(-3)) == Duration.ZERO,
        Duration(Rational(-1, 2)) == Duration.ZERO,
      )
    }
    test("multiply") {
      assert(
        Duration(Rational(1, 2)) * 2 == Duration(Rational(1)),
        Duration(Rational(1, 2)) * -2 == Duration.ZERO
      )
    }
  }

}
