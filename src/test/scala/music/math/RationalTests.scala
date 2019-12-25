package music.math

import utest._

object RationalTests extends TestSuite {

  val tests: Tests = Tests {
    test("construct whole numbers") {
      assert(Rational(2) == Rational(2, 1))
    }
    test("simplify") {
      assert(
        Rational(2, 4) == Rational(1, 2),
        Rational(8, 24) == Rational(1, 3)
      )
    }
    test("reciprocal values") {
      assert(
        Rational.reciprocal(7) == Rational(1, 7)
      )
    }
    test("addition") {
      assert(
        Rational(1, 2) + Rational(1, 4) == Rational(3, 4),
        Rational(3, 5) + Rational(1, 10) == Rational(7, 10)
      )
    }
    test("subtraction") {
      assert(
        Rational(1, 2) - Rational(1, 4) == Rational(1, 4),
        Rational(3, 5) - Rational(1, 10) == Rational(1, 2)
      )
    }
    test("division") {
      Rational(1, 2) / 2 == Rational(1, 4)
      Rational(0) / 2 == Rational(0)
      Rational(1, 2) / 0 == Rational(0)
    }
    test("multiplication") {
      Rational(1, 2) * 2 == Rational(1)
      Rational(3, 7) * 2 == Rational(6, 7)
      Rational(1, 4) * Rational(3, 5) == Rational(3, 20)
    }
    test("compare") {
      assert(
        Rational(1, 2).compare(Rational(1, 3)) == 1,
        Rational(1, 3).compare(Rational(1, 2)) == -1,
        Rational(1, 2).compare(Rational(1, 2)) == 0
      )
    }
    test("string representation") {
      assert(
        Rational(4, 0).toString == "0",
        Rational(0, 4).toString == "0",
        Rational(1).toString == "1",
        Rational(1, 2).toString == "1/2",
        Rational(3, 2).toString == "1 1/2",
        Rational(320, 25).toString == "12 4/5"
      )
    }
  }

}
