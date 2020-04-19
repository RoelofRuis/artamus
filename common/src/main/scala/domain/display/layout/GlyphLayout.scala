package domain.display.layout

import domain.display.glyph.Glyphs.{Glyph, SingleGlyph}
import domain.math.temporal.{Position, Window}

object GlyphLayout {

  def layoutGlyphs[A](
    elements: Seq[Windowed[A]],
    restGlyph: A,
    grid: Grid
  ): Seq[Glyph[A]] = {
    elements
      .foldLeft(List[Windowed[A]]()) {
        case (acc, windowed) => acc
          .lastOption
          .map(_.window)
          .getOrElse(Window.instantAt(Position.ZERO))
          .until(windowed.window)  match {
          case Some(restWindow) => acc :+ Windowed(restWindow, restGlyph) :+ windowed
          case None => acc :+ windowed
        }
      }
      .foldLeft(List[Glyph[A]]()) { case (acc, windowed) =>
        acc ++ grid
          .getBars(windowed.window)
          .flatMap { bar =>
            bar
              .fitGlyphDurations(windowed.window)
              .map(duration => SingleGlyph(windowed.glyph, duration))
          }
      }
  }

}