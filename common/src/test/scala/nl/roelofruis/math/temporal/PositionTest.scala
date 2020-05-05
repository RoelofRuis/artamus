package nl.roelofruis.math.temporal

import nl.roelofruis.math.Rational
import utest._

object PositionTest extends TestSuite {

  // TODO: add more cases

  val tests: Tests = Tests {
    test("zero") {
      assert(Position.ZERO == Position(Rational(0)))
    }
    test("compare") {
      assert(
        Position(Rational(-1)).compare(Position.ZERO) == -1,
        Position.ZERO.compare(Position(Rational(-1))) == 1,
        Position.ZERO.compare(Position.ZERO) == 0
      )
    }
  }

}
