package domain.math.temporal

import domain.math.Rational
import utest._

object WindowTest extends TestSuite {

  val tests: Tests = Tests {
    test("window ends correctly") {
      assert(Window.instantAt(Position.ZERO).end == Position.ZERO)
      assert(Window(Position.ZERO, Duration(Rational(5, 8))).end == Position(Rational(5,8)))
    }
  }

}
