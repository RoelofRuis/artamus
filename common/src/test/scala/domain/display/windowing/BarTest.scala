package domain.display.windowing

import domain.display.glyph.Glyphs.{GlyphDuration, SingleGlyph}
import domain.display.layout.{Bar, Windowed}
import domain.math.Rational
import domain.math.temporal.{Duration, Position, Window}
import utest._

object BarTest extends TestSuite {

  final case class TestGlyph()

  private def windowed(windowStart: Rational, windowStop: Rational): Windowed[TestGlyph] = {
    Windowed(
      Window(Position(windowStart), Duration(windowStop - windowStart)),
      TestGlyph()
    )
  }

  private def singleSpan(n: Int, dots: Int, tieToNext: Boolean = false): SingleGlyph[TestGlyph] = {
    SingleGlyph(TestGlyph(), GlyphDuration(n, dots, tieToNext))
  }

  val bar1: Bar = Bar(
    Window(Position.ZERO, Duration(Rational(1)))
  )

  println(bar1.getBarSpans(windowed(Rational(0), Rational(5, 8))))

  val tests: Tests = Tests {
    test("target window outside bar") {
      assert(bar1.getBarSpans(windowed(Rational(1), Rational(2))) == Vector.empty)
    }
    test("target window covering the complete window") {
      assert(bar1.getBarSpans(windowed(Rational(0), Rational(1))) == Vector(singleSpan(0, 0)))
    }
    test("target window covering the first half of the window") {
      assert(bar1.getBarSpans(windowed(Rational(0), Rational(1, 2))) == Vector(singleSpan(1, 0)))
    }
    test("target window covering the last half of the window") {
      assert(bar1.getBarSpans(windowed(Rational(1, 2), Rational(1))) == Vector(singleSpan(1, 0)))
    }
    test("target window passes end of bar") {
      assert(bar1.getBarSpans(windowed(Rational(1, 2), Rational(3, 2))) == Vector(singleSpan(1, 0, tieToNext=true)))
    }
    test("target window covering 3/8") {
      assert(bar1.getBarSpans(windowed(Rational(0), Rational(3, 8))) == Vector(singleSpan(2, 1)))
    }
    test("target window covering 5/8") {
      assert(bar1.getBarSpans(windowed(Rational(0), Rational(5, 8))) ==
        Vector(singleSpan(1, 0, tieToNext=true), singleSpan(3, 0))
      )
    }
    test("target window covering 7/8") {
      assert(bar1.getBarSpans(windowed(Rational(0), Rational(7, 8))) == Vector(singleSpan(1, 2)))
    }
  }

}
