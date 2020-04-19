package domain.display.layout

import domain.display.glyph.Glyphs.GlyphDuration
import domain.math.Rational
import domain.math.temporal.Window

final case class Bar(barWindow: Window) {

  // Er zijn vier hoofdsituaties:
  // Een noot past en kan precies volgens zijn lengte worden opgeschreven
  // Een noot past niet omdat het grid onderbreekt en moet worden opgedeeld en overgebonden
  // Een noot past niet omdat de lengte niet is op te schrijven moet worden opgedeeld en overgebonden
  // Een noot past niet omdat de duur niet goed is, en moet in een tuplet samen met andere noten worden ondergebracht

  import domain.math.IntegerMath

  def fitGlyphDurations(window: Window): Vector[GlyphDuration] = {
    val durations: Vector[GlyphDuration] = barWindow.intersectNonInstant(window) match {
      case None => Vector.empty
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

    (window.endsLaterThan(barWindow), durations.lastOption) match {
      case (true, Some(lastDuration)) => durations.updated(durations.length-1, lastDuration.copy(tieToNext=true))
      case _ => durations
    }
  }
}
