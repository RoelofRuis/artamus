package nl.roelofruis.artamus.core.layout

import nl.roelofruis.artamus.core.common.Containers.Windowed
import nl.roelofruis.artamus.core.layout.ChordStaffGlyph.{ChordNameGlyph, ChordRestGlyph}
import nl.roelofruis.artamus.core.layout.Layout.Element
import nl.roelofruis.artamus.core.layout.Staff.{ChordStaff, StaffGroup}
import nl.roelofruis.artamus.core.track.Layer.ChordLayer
import nl.roelofruis.artamus.core.common.Containers._

object DisplayableLayers {

  implicit class DisplayableChordLayer(layer: ChordLayer) {
    lazy val elementIterator: Seq[Element[ChordStaffGlyph]] = layer.chords.map {
      case (_, Windowed(window, chord)) =>
        Element[ChordStaffGlyph](window, ChordNameGlyph(chord.root, chord.quality))
    }.toSeq

    lazy val layout: LayoutDescription[ChordStaffGlyph] = LayoutDescription(
      layer.metres.iteratePositioned,
      ChordRestGlyph()
    )

    def display: StaffGroup = {
      Seq(
        ChordStaff(
          Layout.layoutElements(elementIterator, layout)
        )
      )
    }
  }

}
