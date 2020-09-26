package nl.roelofruis.artamus.core.layout.algorithms

import nl.roelofruis.artamus.core.common.Containers.{Windowed, _}
import nl.roelofruis.artamus.core.common.Position
import nl.roelofruis.artamus.core.layout.ChordStaffGlyph.{ChordNameGlyph, ChordRestGlyph}
import nl.roelofruis.artamus.core.layout.Glyph.InstantGlyph
import nl.roelofruis.artamus.core.layout.RNAStaffGlyph.{DegreeGlyph, RNARestGlyph}
import nl.roelofruis.artamus.core.layout.Staff.{ChordStaff, NoteStaff, RNAStaff, StaffGroup}
import nl.roelofruis.artamus.core.layout.StaffGlyph.{KeyGlyph, NoteGroupGlyph, RestGlyph, TimeSignatureGlyph}
import nl.roelofruis.artamus.core.layout.{ChordStaffGlyph, Glyph, RNAStaffGlyph, StaffGlyph}
import nl.roelofruis.artamus.core.track.Layer.{ChordLayer, KeySeq, MetreSeq, NoteLayer, RNALayer}
import nl.roelofruis.artamus.core.track.Temporal.Metre
import nl.roelofruis.artamus.core.track.Track
import nl.roelofruis.artamus.core.track.algorithms.TemporalMaths
import nl.roelofruis.artamus.core.common.Maths._

object DisplayableLayers extends TemporalMaths {

  def displayRNALayer(track: Track, layer: RNALayer): StaffGroup = {
    lazy val elementIterator: WindowedSeq[RNAStaffGlyph] = layer.analysis.map {
      case Windowed(window, analysedChord) =>
        Windowed[RNAStaffGlyph](window, DegreeGlyph(analysedChord.degree))
    }

    lazy val layout: LayoutDescription[RNAStaffGlyph] = LayoutDescription(
      iteratePositioned(track.metres),
      RNARestGlyph()
    )

    Seq(RNAStaff(Layout.layoutElements(elementIterator, layout)))
  }

  def displayChordLayer(track: Track, layer: ChordLayer): StaffGroup = {
    lazy val elementIterator: WindowedSeq[ChordStaffGlyph] = layer.chords.map {
      case Windowed(window, chord) =>
        Windowed[ChordStaffGlyph](window, ChordNameGlyph(chord.root, chord.quality))
    }

    lazy val layout: LayoutDescription[ChordStaffGlyph] = LayoutDescription(
      iteratePositioned(track.metres),
      ChordRestGlyph()
    )

    Seq(ChordStaff(Layout.layoutElements(elementIterator, layout)))
  }

  def displayNoteLayer(track: Track, layer: NoteLayer): StaffGroup = {
    def elementIterator: WindowedSeq[StaffGlyph] =
      layer
        .notes
        .map { case Windowed(window, noteGroup) =>
          Windowed[StaffGlyph](window, NoteGroupGlyph(
            noteGroup.map(note => (note.descriptor, note.octave)))
          )
        }

    lazy val layout: LayoutDescription[StaffGlyph] = LayoutDescription(
      iteratePositioned(track.metres),
      RestGlyph(),
      Seq(
        timeSignatureBuilder(track.metres),
        keyBuilder(track.keys)
      )
    )

    Seq(NoteStaff(Layout.layoutElements(elementIterator, layout)))
  }

  private def timeSignatureBuilder(metres: MetreSeq): Position => Option[Glyph[StaffGlyph]] = pos => {
    metres.find(_.window.start == pos)
      .map { metre =>
        val (num, denom) = metre.get.timeSignatureFraction
        InstantGlyph(TimeSignatureGlyph(num, 2**denom))
      }
  }

  private def keyBuilder(keys: KeySeq): Position => Option[Glyph[StaffGlyph]] = pos => {
    keys.find(_.window.start == pos)
      .map { key =>
        InstantGlyph(KeyGlyph(key.get.root, key.get.scale))
      }
  }

  // TODO: this can be generalized to work on any inner type that has a duration.
  private def iteratePositioned(metres: MetreSeq): LazyList[Windowed[Metre]] = {
    val active = metres.head

    def loop(searchPos: Position): LazyList[Windowed[Metre]] = {
      // TODO: add a duration and make lookup actually work..!
      Windowed(searchPos, active.get.duration, active.get) #:: loop(searchPos + active.get.duration)
    }

    loop(Position.ZERO)
  }

}
