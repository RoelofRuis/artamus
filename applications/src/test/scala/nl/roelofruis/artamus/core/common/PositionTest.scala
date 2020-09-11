package nl.roelofruis.artamus.core.common

import utest._

object PositionTest extends TestSuite {

  // TODO: add more cases

  val tests: Tests = Tests {
    test("zero") {
      assert(Position.ZERO == Position(0))
    }
    test("compare") {
      assert(
        Position(-1).compare(Position.ZERO) == -1,
        Position.ZERO.compare(Position(-1)) == 1,
        Position.ZERO.compare(Position.ZERO) == 0
      )
    }
  }

}
