package domain.display.layout

import domain.display.glyph.Glyphs.{Glyph, GlyphDuration, SingleGlyph}
import domain.display.layout.Bars.Bar
import domain.math.Rational
import domain.math.temporal.{Position, Window}

import scala.annotation.tailrec

object BarLayout {

  import domain.math.IntegerMath

  final case class BarElements[A](bar: Bar, elements: Seq[Windowed[A]])

  final case object PlaceholderRest

  // Er zijn vier hoofdsituaties:
  // Een noot past en kan precies volgens zijn lengte worden opgeschreven
  // Een noot past niet omdat het grid onderbreekt en moet worden opgedeeld en overgebonden
  // Een noot past niet omdat de lengte niet is op te schrijven moet worden opgedeeld en overgebonden
  // Een noot past niet omdat de duur niet goed is, en moet in een tuplet samen met andere noten worden ondergebracht

  type GlyphSequence[A] = Seq[Glyph[Either[PlaceholderRest.type, A]]]

  implicit class GlyphFitting[A](barElements: BarElements[A]) {

    final val barWindow = barElements.bar.barWindow

    def glyphSequence: GlyphSequence[A] = {

      @tailrec
      def loop(acc: GlyphSequence[A], position: Position, elements: Seq[Windowed[A]]): GlyphSequence[A] = {
        elements.headOption match {
          case None =>
            val finalRest = fitDuration(Window(position, barWindow.end - position)).map { dur =>
              SingleGlyph[Either[PlaceholderRest.type, A]](Left(PlaceholderRest), dur)
            }
            acc ++ finalRest

          case Some(Windowed(window, g)) =>
            val windowUntilNext = Window(position, window.start - position)

            val restBefore = fitDuration(windowUntilNext).map { dur => SingleGlyph[Either[PlaceholderRest.type, A]](Left(PlaceholderRest), dur) }
            val element = fitDuration(window).map { dur => SingleGlyph[Either[PlaceholderRest.type, A]](Right(g), dur) }

            loop(acc ++ restBefore ++ element, window.end, elements.tail)
        }
      }

      loop(Seq(), barWindow.start, barElements.elements)
    }

    def fitDuration(window: Window): Seq[GlyphDuration] = {
      barWindow.intersectNonInstant(window) match {
        case None => Seq.empty
        case Some(windowWithinBar) =>
          windowWithinBar.duration.v match {
            case r @ Rational(_, d) if ! d.isPowerOfTwo =>
              // This is tuplet
              throw new NotImplementedError(s"Cannot fit tuplet length [$r] in bar")

            case Rational(1, d) =>
              Vector(GlyphDuration(d.largestPowerOfTwo, 0))

            case Rational(3, d) =>
              Vector(GlyphDuration(d.largestPowerOfTwo - 1, 1))

            case Rational(5, d) => Vector(
              GlyphDuration(d.largestPowerOfTwo - 2, 0, tieToNext=true),
              GlyphDuration(d.largestPowerOfTwo, 0)
            )

            case Rational(7, d) =>
              Vector(GlyphDuration(d.largestPowerOfTwo - 2, 2))

            case r =>
              // Other unknown cases...
              throw new NotImplementedError(s"Cannot fit length [$r] in bar")
          }
      }
    }

  }
}
