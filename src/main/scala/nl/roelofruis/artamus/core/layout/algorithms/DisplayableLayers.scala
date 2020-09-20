package nl.roelofruis.artamus.core.layout.algorithms

import nl.roelofruis.artamus.core.common.Containers.{Windowed, _}
import nl.roelofruis.artamus.core.common.Position
import nl.roelofruis.artamus.core.layout.ChordStaffGlyph.{ChordNameGlyph, ChordRestGlyph}
import nl.roelofruis.artamus.core.layout.Staff.{ChordStaff, NoteStaff, StaffGroup}
import nl.roelofruis.artamus.core.layout.StaffGlyph.{NoteGroupGlyph, RestGlyph}
import nl.roelofruis.artamus.core.layout.{ChordStaffGlyph, StaffGlyph}
import nl.roelofruis.artamus.core.track.Layer.{ChordLayer, NoteLayer}
import nl.roelofruis.artamus.core.track.Temporal.Metre
import nl.roelofruis.artamus.core.track.transform.TemporalMaths

object DisplayableLayers extends TemporalMaths {

  def displayChordLayer(layer: ChordLayer, metres: TemporalInstantMap[Metre]): StaffGroup = {
    lazy val elementIterator: WindowedSeq[ChordStaffGlyph] = layer.chords.map {
      case (_, Windowed(window, chord)) =>
        Windowed[ChordStaffGlyph](window, ChordNameGlyph(chord.root, chord.quality))
    }.toSeq

    lazy val layout: LayoutDescription[ChordStaffGlyph] = LayoutDescription(
      iteratePositioned(metres),
      ChordRestGlyph()
    )

    Seq(ChordStaff(Layout.layoutElements(elementIterator, layout)))
  }

  def displayNoteLayer(layer: NoteLayer, metres: TemporalInstantMap[Metre]): StaffGroup = {
    def elementIterator: WindowedSeq[StaffGlyph] =
      layer
        .notes
        .values
        .toSeq
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

  private def iteratePositioned(metres: TemporalInstantMap[Metre]): LazyList[Positioned[Metre]] = {
    val (_, active) = metres.head

    def loop(searchPos: Position): LazyList[Positioned[Metre]] = {
      // TODO: add a duration and make lookup actually work..!
      Positioned(searchPos, active) #:: loop(searchPos + active.duration)
    }

    loop(Position.ZERO)
  }

}
