package nl.roelofruis.artamus.core.math

import utest._

object RationalTest extends TestSuite {

  val tests: Tests = Tests {
    test("construct whole numbers") {
      assert(
        Rational(2) == Rational(2, 1),
        Rational(-3) == Rational(-3, 1)
      )
    }
    test("negative numbers") {
      assert(
        Rational(2, -4) == Rational(-2, 4),
        Rational(-2, -4) == Rational(2, 4)
      )
    }
    test("simplify") {
      assert(
        Rational(2, 4) == Rational(1, 2),
        Rational(8, 24) == Rational(1, 3),
        Rational(2, -4) == Rational(-1, 2),
        Rational(-2, 4) == Rational(-1, 2)
      )
    }
    test("instantiate reciprocal") {
      assert(
        Rational.reciprocal(7) == Rational(1, 7),
        Rational.reciprocal(-7) == Rational(-1, 7)
      )
    }
    test("reciprocal value") {
      assert(
        Rational(1, 4).reciprocal == Rational(4, 1),
        Rational(7, 13).reciprocal == Rational(13, 7),
        Rational(0, 0).reciprocal == Rational(0),
        Rational(-1, 2).reciprocal == Rational(-2, 1)
      )
    }
    test("addition") {
      assert(
        Rational(1, 2) + Rational(1, 4) == Rational(3, 4),
        Rational(3, 5) + Rational(1, 10) == Rational(7, 10),
        Rational(1, 10) + Rational(3, 5) == Rational(7, 10)
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
      Rational(3, 5) * Rational(1, 4) == Rational(3, 20)
      Rational(1, 2) * Rational(-1, 2) == Rational(-1, 4)
      Rational(-1, 2) * Rational(1, 2) == Rational(-1, 4)
      Rational(-1, 2) * Rational(-1, 2) == Rational(1, 4)
    }
    test("compare") {
      assert(
        Rational(1, 2).compare(Rational(1, 3)) == 1,
        Rational(1, 3).compare(Rational(1, 2)) == -1,
        Rational(1, 2).compare(Rational(1, 2)) == 0,
        Rational(-1, 2).compare(Rational(1, -2)) == 0
      )
    }
    test("string representation") {
      assert(
        Rational(4, 0).toString == "0",
        Rational(0, 4).toString == "0",
        Rational(1).toString == "1",
        Rational(1, 2).toString == "1/2",
        Rational(3, 2).toString == "1 1/2",
        Rational(320, 25).toString == "12 4/5",
        Rational(-4, 0).toString == "0",
        Rational(-1).toString == "-1",
        Rational(-1, 2).toString == "-1/2",
        Rational(-3, 2).toString == "-1 1/2",
        Rational(3, -2).toString == "-1 1/2",
      )
    }
    test("cast to double") {
      assert(
        Rational(0).toDouble < 1e-100,
        Rational(1).toDouble - 1 < 1e-100,
        Rational(1, 2).toDouble - 0.5 < 1e-100,
        Rational(1, 3).toDouble - 0.3333333333 < 1e-10,
        Rational(22, 7).toDouble - 3.142 < 1e-3,
        Rational(355, 113).toDouble - 3.1415929 < 1e-7
      )
    }
  }

}
