package nl.roelofruis.artamus.core.layout.algorithms

import nl.roelofruis.artamus.core.common.Containers.{Windowed, _}
import nl.roelofruis.artamus.core.common.Position
import nl.roelofruis.artamus.core.layout.ChordStaffGlyph.{ChordNameGlyph, ChordRestGlyph}
import nl.roelofruis.artamus.core.layout.Staff.{ChordStaff, StaffGroup}
import nl.roelofruis.artamus.core.layout.algorithms.Layout.Element
import nl.roelofruis.artamus.core.layout.ChordStaffGlyph
import nl.roelofruis.artamus.core.track.Layer.ChordLayer
import nl.roelofruis.artamus.core.track.Temporal.Metre
import nl.roelofruis.artamus.core.track.analysis.TemporalMaths

object DisplayableLayers extends TemporalMaths {

  implicit class DisplayableChordLayer(layer: ChordLayer) {
    lazy val elementIterator: Seq[Element[ChordStaffGlyph]] = layer.chords.map {
      case (_, Windowed(window, chord)) =>
        Element[ChordStaffGlyph](window, ChordNameGlyph(chord.root, chord.quality))
    }.toSeq

    lazy val layout: LayoutDescription[ChordStaffGlyph] = LayoutDescription(
      iteratePositioned(layer.metres),
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

  private def iteratePositioned: TemporalInstantMap[Metre] => LazyList[Positioned[Metre]] = map => {
    val (_, active) = map.head

    def loop(searchPos: Position): LazyList[Positioned[Metre]] = {
      // TODO: add a duration and make lookup actually work..!
      Positioned(searchPos, active) #:: loop(searchPos + active.duration)
    }

    loop(Position.ZERO)
  }

}
