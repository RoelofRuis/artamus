package domain.display.layout

import domain.display.glyph.Glyphs.Glyph
import domain.display.layout.MetrePositioning.PositionedMetre
import domain.math.temporal.Position

final case class LayoutDescription[A](
  metres: LazyList[PositionedMetre],
  restGlyph: A,
  instantGlyphs: Position => Seq[Glyph[A]] = (_: Position) => Seq.empty
)

object LayoutDescription {

  def apply[A](
    metres: LazyList[PositionedMetre],
    restGlyph: A,
    instantGlyphBuilders: Seq[Position => Option[Glyph[A]]]
  ): LayoutDescription[A] = {
    LayoutDescription(metres, restGlyph, pos => instantGlyphBuilders.flatMap { _(pos) })
  }

}