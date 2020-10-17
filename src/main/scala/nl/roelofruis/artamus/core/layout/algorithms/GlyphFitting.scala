package nl.roelofruis.artamus.core.layout.algorithms

import nl.roelofruis.artamus.core.common.Maths._
import nl.roelofruis.artamus.core.common.Temporal.Windowed
import nl.roelofruis.artamus.core.common.{Rational, Window}
import nl.roelofruis.artamus.core.layout.Glyph.GlyphDuration
import nl.roelofruis.artamus.core.track.Temporal.Metre
import nl.roelofruis.artamus.core.track.algorithms.TemporalMaths

object GlyphFitting extends TemporalMaths {

  def fitDurations(metre: Windowed[Metre], window: Window, tie: Boolean = false): Seq[GlyphDuration] = {
    if (metre.get.hasMultiplePulseGroups) {
      val subdivisions = subdivide(metre).filter(_.window.intersectNonInstant(window).nonEmpty)
      subdivisions.init.foldLeft(Seq[GlyphDuration]()) {
        case (acc, subMetre) => acc ++ fitDurations(subMetre, window, tie = true)
      } ++ fitDurations(subdivisions.last, window, tie)
    } else {
      metre.window.intersectNonInstant(window) match {
        case None => Seq.empty
        case Some(window) =>
          window.duration.v match {
            case r @ Rational(_, d) if ! d.isPowerOfTwo =>
              // TODO: implement tuples
              throw new NotImplementedError(s"Cannot fit tuplet [$r]!")

            case Rational(1, d) =>
              Seq(GlyphDuration(d.largestPowerOfTwo, 0, tie))

            case Rational(n, d) =>
              if ((n + 1).isPowerOfTwo) { // Can be written with dots
                val numDots = (n + 1).largestPowerOfTwo - 1
                Seq(GlyphDuration(d.largestPowerOfTwo - numDots, n.largestPowerOfTwo, tie))
              } else { // Let subdivisions figure it out
                val subdivisions = subdivide(metre).filter(_.window.intersectNonInstant(window).nonEmpty)
                subdivisions.init.foldLeft(Seq[GlyphDuration]()) {
                  case (acc, subMetre) => acc ++ fitDurations(subMetre, window, tie = true)
                } ++ fitDurations(subdivisions.last, window, tie)
              }

            case r => throw new NotImplementedError(s"Cannot fit length [$r]")
          }
      }
    }
  }

}
