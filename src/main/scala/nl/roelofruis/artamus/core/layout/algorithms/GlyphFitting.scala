package nl.roelofruis.artamus.core.layout.algorithms

import nl.roelofruis.artamus.core.common.Maths._
import nl.roelofruis.artamus.core.common.Temporal.Windowed
import nl.roelofruis.artamus.core.common.{Rational, Window}
import nl.roelofruis.artamus.core.layout.Glyph.GlyphDuration
import nl.roelofruis.artamus.core.track.Temporal.Metre
import nl.roelofruis.artamus.core.track.algorithms.TemporalMaths

object GlyphFitting extends TemporalMaths {

  def fitDurations(metre: Windowed[Metre], elementWindow: Window, tie: Boolean = false): Seq[GlyphDuration] = {
    if (metre.get.hasMultiplePulseGroups) subdivideAndFit(metre, elementWindow, tie)
    else {
      metre.window.intersectNonInstant(elementWindow) match {
        case None => Seq.empty
        case Some(window) =>
          window.duration.v match {
            case r @ Rational(_, d) if ! d.isPowerOfTwo =>
              // TODO: implement tuples
              throw new NotImplementedError(s"Cannot fit tuplet [$r]!")

            case Rational(1, d) => // Can be written as simple whole duration
              Seq(GlyphDuration(d.largestPowerOfTwo, 0, tie))

            case Rational(n, d) if (n + 1).isPowerOfTwo => // Can be written with dots
              val numDots = (n + 1).largestPowerOfTwo - 1
              Seq(GlyphDuration(d.largestPowerOfTwo - numDots, n.largestPowerOfTwo, tie))

            case _ => subdivideAndFit(metre, elementWindow, tie) // Let subdivisions figure it out
          }
      }
    }
  }

  private def subdivideAndFit(metre: Windowed[Metre], elementWindow: Window, tie: Boolean): Seq[GlyphDuration] = {
    val subdivisions = subdivide(metre).filter(_.window.intersectNonInstant(elementWindow).nonEmpty)

    subdivisions
      .init
      .foldLeft(Seq[GlyphDuration]()) { case (acc, subMetre) =>
        acc ++ fitDurations(subMetre, elementWindow, tie = true)
      } ++ fitDurations(subdivisions.last, elementWindow, tie)
  }

  private def subdivide(metre: Windowed[Metre]): Seq[Windowed[Metre]] = {
    val pulseGroups = metre.get.pulseGroups
    // TODO: generalize!
    if (pulseGroups.size > 2) throw new NotImplementedError(s"Cannot subdivide metre [${metre.get}]")

    if (pulseGroups.size == 2) {
      Seq(
        Windowed(metre.window.start, pulseGroups.head.duration, Metre(Seq(pulseGroups.head))),
        Windowed(metre.window.start + pulseGroups.head.duration, pulseGroups(1).duration, Metre(Seq(pulseGroups(1))))
      )
    } else {
      val pulseGroup = pulseGroups.head
      val newPulseGroup = pulseGroup.copy(baseDuration = pulseGroup.baseDuration + 1)
      Seq(
        Windowed(metre.window.start, newPulseGroup.duration, Metre(Seq(newPulseGroup))),
        Windowed(metre.window.start + newPulseGroup.duration, newPulseGroup.duration, Metre(Seq(newPulseGroup)))
      )
    }
  }

}
