package domain.display.layout

import domain.display.glyph.Glyphs.{Glyph, GlyphDuration, SingleGlyph}
import domain.display.layout.BarLayout.{BarElements, PlaceholderRest}
import domain.display.layout.Bars.Bar
import domain.math.Rational
import domain.math.temporal.{Duration, Position, Window}
import utest._

object BarLayoutTest extends TestSuite {

  type TestGlyphType = Int

  private def barElements(w: Seq[(Rational, Rational)] = Seq()): BarElements[TestGlyphType] = {
    BarElements(
      Bar(Window(Position.ZERO, Duration(Rational(1)))),
      w.zipWithIndex.map { case ((from, to), i) =>
        Windowed(Window(Position(from), Duration(to - from)), i)
      }
    )
  }

  private def restGlyph(n: Int, dots: Int, tie: Boolean = false): Glyph[Either[PlaceholderRest.type, TestGlyphType]] = {
    SingleGlyph(Left(PlaceholderRest), GlyphDuration(n, dots, tie))
  }

  private def glyph(glyphNumber: Int, n: Int, dots: Int, tie: Boolean = false): Glyph[Either[PlaceholderRest.type, TestGlyphType]] = {
    SingleGlyph(Right(glyphNumber), GlyphDuration(n, dots, tie))
  }

  val tests: Tests = Tests {
    test("empty bar") {
      assert(barElements().glyphSequence == Seq(restGlyph(0, 0)))
    }
    test("bar starts with half note") {
      assert(barElements(Seq((Rational(0), Rational(1, 2)))).glyphSequence ==
        Seq(glyph(0, 1, 0), restGlyph(1, 0)))
    }
    test("bar ends with half note") {
      assert(barElements(Seq((Rational(1, 2), Rational(1)))).glyphSequence ==
        Seq(restGlyph(1, 0), glyph(0, 1, 0)))
    }
  }

}
