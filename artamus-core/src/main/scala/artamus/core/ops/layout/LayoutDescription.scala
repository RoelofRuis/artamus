package artamus.core.ops.layout

import artamus.core.model.display.glyph.Glyphs.Glyph
import artamus.core.ops.layout.MetrePositioning.PositionedMetre
import nl.roelofruis.math.temporal.Position

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