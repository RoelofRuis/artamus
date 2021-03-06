package nl.roelofruis.artamus.core.common

import utest._

object WindowTest extends TestSuite {

  final val window0 = Window.instantAt(Position(1, 8))
  final val window1 = Window(Position.ZERO, Duration(Rational(1, 8)))
  final val window2 = Window(Position(1, 2), Duration(Rational(1, 4)))

  val tests: Tests = Tests {
    test("window ends correctly") {
      assert(window0.end == Position(1, 8))
      assert(window1.end == Position(1, 8))
      assert(window2.end == Position(3, 4))
    }
    test("window until") {
      assert(window1.until(window2).contains(Window(Position(1, 8), Duration(Rational(3, 8)))))
      assert(window2.until(window1).isEmpty)
    }
    test("window intersects") {
      assert(window0.intersect(window1).contains(Window.instantAt(Position(1, 8))))
      assert(window1.intersect(window2).isEmpty)
      assert(window2.intersect(window1).isEmpty)
    }
    test("window intersect non instant") {
      assert(window0.intersectNonInstant(window1).isEmpty)
      assert(window1.intersectNonInstant(window2).isEmpty)
      assert(window2.intersectNonInstant(window1).isEmpty)
    }
    test("is instant") {
      assert(window0.isInstant)
      assert( ! window1.isInstant)
      assert( ! window2.isInstant)
    }
    test("stretches to") {
      assert(window1.stretchTo(Position(1, 4)) == Window(Position.ZERO, Duration(Rational(1, 4))))
      assert(window1.stretchTo(Position(1, 16)) == Window(Position.ZERO, Duration(Rational(1, 16))))
      assert(window2.stretchTo(Position(1,4)) == Window(Position(1, 2), Duration.ZERO))
    }
  }

}
