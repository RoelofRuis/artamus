package nl.roelofruis.artamus.core.layout.algorithms

import nl.roelofruis.artamus.core.common.Maths._
import nl.roelofruis.artamus.core.common.Position
import nl.roelofruis.artamus.core.common.Temporal.{Windowed, _}
import nl.roelofruis.artamus.core.layout.ChordStaffGlyph.{ChordNameGlyph, ChordRestGlyph}
import nl.roelofruis.artamus.core.layout.Glyph.InstantGlyph
import nl.roelofruis.artamus.core.layout.RNAStaffGlyph.{DegreeGlyph, KeyIndicatorGlyph, RNARestGlyph}
import nl.roelofruis.artamus.core.layout.Staff.{ChordStaff, NoteStaff, RNAStaff, StaffGroup}
import nl.roelofruis.artamus.core.layout.StaffGlyph.{KeyGlyph, NoteGroupGlyph, RestGlyph, TimeSignatureGlyph}
import nl.roelofruis.artamus.core.layout.{ChordStaffGlyph, Glyph, RNAStaffGlyph, StaffGlyph}
import nl.roelofruis.artamus.core.track.Layer._
import nl.roelofruis.artamus.core.track.Track
import nl.roelofruis.artamus.core.track.algorithms.TemporalMaths

object DisplayableLayers extends TemporalMaths {

  def displayRNALayer(track: Track, layer: RNALayer): StaffGroup = {
    lazy val elementIterator: Timeline[RNAStaffGlyph] = layer.analysis.map {
      case Windowed(window, analysedChord) =>
        Windowed[RNAStaffGlyph](window, DegreeGlyph(analysedChord.degree))
    }

    lazy val layout: LayoutDescription[RNAStaffGlyph] = LayoutDescription(
      track.metres.iterateWindowed,
      RNARestGlyph(),
      Seq(
        keyIndicatorBuilder(layer.keys)
      )
    )

    Seq(RNAStaff(Layout.layoutElements(elementIterator, layout)))
  }

  def displayChordLayer(track: Track, layer: ChordLayer): StaffGroup = {
    lazy val elementIterator: Timeline[ChordStaffGlyph] = layer.chords.map {
      case Windowed(window, chord) =>
        Windowed[ChordStaffGlyph](window, ChordNameGlyph(chord.root, chord.quality))
    }

    lazy val layout: LayoutDescription[ChordStaffGlyph] = LayoutDescription(
      track.metres.iterateWindowed,
      ChordRestGlyph()
    )

    Seq(ChordStaff(Layout.layoutElements(elementIterator, layout)))
  }

  def displayNoteLayer(track: Track, layer: NoteLayer): StaffGroup = {
    def elementIterator: Timeline[StaffGlyph] =
      layer
        .notes
        .map { case Windowed(window, noteGroup) =>
          Windowed[StaffGlyph](window, NoteGroupGlyph(
            noteGroup.map(note => (note.descriptor, note.octave)))
          )
        }

    lazy val layout: LayoutDescription[StaffGlyph] = LayoutDescription(
      track.metres.iterateWindowed,
      RestGlyph(),
      Seq(
        timeSignatureBuilder(track.metres),
        keyBuilder(track.keys)
      )
    )

    Seq(NoteStaff(Layout.layoutElements(elementIterator, layout)))
  }

  private def keyIndicatorBuilder(keys: KeySeq): Position => Option[Glyph[RNAStaffGlyph]] = pos => {
    keys.asSeq.find(_.position == pos)
      .map { key => InstantGlyph(KeyIndicatorGlyph(key.get)) }
  }

  private def timeSignatureBuilder(metres: MetreSeq): Position => Option[Glyph[StaffGlyph]] = pos => {
    metres.asSeq.find(_.position == pos)
      .map { metre =>
        val (num, denom) = metre.get.timeSignatureFraction
        InstantGlyph(TimeSignatureGlyph(num, 2**denom))
      }
  }

  private def keyBuilder(keys: KeySeq): Position => Option[Glyph[StaffGlyph]] = pos => {
    keys.asSeq.find(_.position == pos)
      .map { key => InstantGlyph(KeyGlyph(key.get)) }
  }

}
