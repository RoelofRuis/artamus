package nl.roelofruis.artamus.core.layout.algorithms

import nl.roelofruis.artamus.core.common.Containers.{Windowed, _}
import nl.roelofruis.artamus.core.common.Position
import nl.roelofruis.artamus.core.layout.ChordStaffGlyph.{ChordNameGlyph, ChordRestGlyph}
import nl.roelofruis.artamus.core.layout.Staff.{ChordStaff, NoteStaff, StaffGroup}
import nl.roelofruis.artamus.core.layout.StaffGlyph.{NoteGroupGlyph, RestGlyph}
import nl.roelofruis.artamus.core.layout.{ChordStaffGlyph, StaffGlyph}
import nl.roelofruis.artamus.core.track.Layer.{ChordLayer, MetreTrack, NoteLayer}
import nl.roelofruis.artamus.core.track.Temporal.Metre
import nl.roelofruis.artamus.core.track.algorithms.TemporalMaths

object DisplayableLayers extends TemporalMaths {

  def displayChordLayer(layer: ChordLayer, metres: MetreTrack): StaffGroup = {
    lazy val elementIterator: WindowedSeq[ChordStaffGlyph] = layer.chords.map {
      case Windowed(window, chord) =>
        Windowed[ChordStaffGlyph](window, ChordNameGlyph(chord.root, chord.quality))
    }

    lazy val layout: LayoutDescription[ChordStaffGlyph] = LayoutDescription(
      iteratePositioned(metres),
      ChordRestGlyph()
    )

    Seq(ChordStaff(Layout.layoutElements(elementIterator, layout)))
  }

  def displayNoteLayer(layer: NoteLayer, metres: MetreTrack): StaffGroup = {
    def elementIterator: WindowedSeq[StaffGlyph] =
      layer
        .notes
        .map { case Windowed(window, noteGroup) =>
          Windowed[StaffGlyph](window, NoteGroupGlyph(
            noteGroup.map(note => (note.descriptor, note.octave)))
          )
        }

    lazy val layout: LayoutDescription[StaffGlyph] = LayoutDescription(
      iteratePositioned(metres),
      RestGlyph(),
      Seq() // TODO: add time signature and metres builder
    )

    Seq(NoteStaff(Layout.layoutElements(elementIterator, layout)))
  }

  private def iteratePositioned(metres: MetreTrack): LazyList[Windowed[Metre]] = {
    val active = metres.head

    def loop(searchPos: Position): LazyList[Windowed[Metre]] = {
      // TODO: add a duration and make lookup actually work..!
      Windowed(searchPos, active.get.duration, active.get) #:: loop(searchPos + active.get.duration)
    }

    loop(Position.ZERO)
  }

}