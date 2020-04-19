package domain.display.windowing

import domain.display.glyph.Glyphs.GlyphDuration
import domain.display.layout.Bar
import domain.math.Rational
import domain.math.temporal.{Duration, Position, Window}
import utest._

object BarTest extends TestSuite {

  private def window(windowStart: Rational, windowStop: Rational): Window = {
    Window(Position(windowStart), Duration(windowStop - windowStart))
  }

  private def duration(n: Int, dots: Int, tieToNext: Boolean = false): GlyphDuration = {
    GlyphDuration(n, dots, tieToNext)
  }

  val bar1: Bar = Bar(
    Window(Position.ZERO, Duration(Rational(1)))
  )

  println(bar1.fitGlyphDurations(window(Rational(0), Rational(5, 8))))

  val tests: Tests = Tests {
    test("target window outside bar") {
      assert(bar1.fitGlyphDurations(window(Rational(1), Rational(2))) == Vector.empty)
    }
    test("target window covering the complete window") {
      assert(bar1.fitGlyphDurations(window(Rational(0), Rational(1))) == Vector(duration(0, 0)))
    }
    test("target window covering the first half of the window") {
      assert(bar1.fitGlyphDurations(window(Rational(0), Rational(1, 2))) == Vector(duration(1, 0)))
    }
    test("target window covering the last half of the window") {
      assert(bar1.fitGlyphDurations(window(Rational(1, 2), Rational(1))) == Vector(duration(1, 0)))
    }
    test("target window passes end of bar") {
      assert(bar1.fitGlyphDurations(window(Rational(1, 2), Rational(3, 2))) == Vector(duration(1, 0, tieToNext=true)))
    }
    test("target window covering 3/8") {
      assert(bar1.fitGlyphDurations(window(Rational(0), Rational(3, 8))) == Vector(duration(2, 1)))
    }
    test("target window covering 5/8") {
      assert(bar1.fitGlyphDurations(window(Rational(0), Rational(5, 8))) ==
        Vector(duration(1, 0, tieToNext=true), duration(3, 0))
      )
    }
    test("target window covering 7/8") {
      assert(bar1.fitGlyphDurations(window(Rational(0), Rational(7, 8))) == Vector(duration(1, 2)))
    }
  }

}
