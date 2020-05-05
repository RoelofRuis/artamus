package nl.roelofruis.artamus.core.ops.layout

import nl.roelofruis.artamus.core.model.display.glyph.ChordStaffGlyphFamily.{ChordNameGlyph, ChordRestGlyph, ChordStaffGlyph}
import nl.roelofruis.artamus.core.model.display.glyph.StaffGlyphFamily
import nl.roelofruis.artamus.core.model.display.glyph.StaffGlyphFamily.{NoteGroupGlyph, RestGlyph, StaffGlyph}
import nl.roelofruis.artamus.core.ops.layout.ElementLayout.Element
import nl.roelofruis.artamus.core.model.display.staff.NoteStaff.{Bass, Treble}
import nl.roelofruis.artamus.core.model.display.staff._
import nl.roelofruis.artamus.core.model.primitives.{MidiNoteNumber, Note, NoteGroup}
import nl.roelofruis.artamus.core.ops.transform.analysis.TwelveTonePitchSpelling
import nl.roelofruis.artamus.core.model.track.layers.{ChordLayer, NoteLayer, RhythmLayer}

object DisplayableLayers {

  import MetrePositioning._

  implicit class DisplayableChordLayer(layer: ChordLayer) {
    private val initialKey = layer.keys.initialKey

    lazy val elementIterator: Seq[Element[ChordStaffGlyph]] = layer.chords.chords.map {
      case (_, (window, chord)) =>
        val spelling = TwelveTonePitchSpelling.spellChord(chord, initialKey)
        Element[ChordStaffGlyph](window, ChordNameGlyph(spelling, chord.functions))
    }.toSeq

    lazy val layout: LayoutDescription[ChordStaffGlyph] = LayoutDescription(layer.metres.iteratePositioned,ChordRestGlyph())

    def display: StaffGroup = {
      StaffGroup(
        ChordStaff(
          ElementLayout.layoutElements(elementIterator, layout)
        )
      )
    }
  }

  implicit class DisplayableRhythmLayer(layer: RhythmLayer) {
    lazy val elementIterator: Seq[Element[StaffGlyph]] = layer.voice.readGroupsList().map { noteGroup =>
      Element[StaffGlyph](noteGroup.window, NoteGroupGlyph(Seq()))
    }

    lazy val layout: LayoutDescription[StaffGlyph] = LayoutDescription[StaffGlyph](
      layer.metres.iteratePositioned,
      RestGlyph(),
      Seq(StaffGlyphFamily.timeSignatureBuilder(layer.metres.metres))
    )

    def display: StaffGroup = {
      StaffGroup(
        RhythmicStaff(
          ElementLayout.layoutElements(elementIterator, layout)
        )
      )
    }
  }

  implicit class DisplayableNoteLayer(layer: NoteLayer) {
    private val initialKey = layer.keys.initialKey

    import nl.roelofruis.artamus.core.ops.transform.analysis.TwelveToneTuning._

    trait InclusionStrategy extends (NoteGroup => Seq[Note])

    private def higherNoteNumbers(bound: Int): InclusionStrategy = noteGroup =>
      noteGroup.notes.filter(note => MidiNoteNumber(note.octave, note.pitchClass).value > bound)

    private def lowerEqualNoteNumbers(bound: Int): InclusionStrategy = noteGroup =>
      noteGroup.notes.filter(note => MidiNoteNumber(note.octave, note.pitchClass).value <= bound)

    def elementIterator(inclusion: InclusionStrategy): Seq[Element[StaffGlyph]] =
      layer
        .defaultVoice
        .readGroupsList()
        .flatMap { noteGroup =>
          inclusion(noteGroup) match {
            case Nil => None
            case filteredNotes => Some(noteGroup.copy(notes=filteredNotes))
          }
        }
        .map { noteGroup =>
          val noteSpellings = noteGroup.notes.map(note => TwelveTonePitchSpelling.spellNote(note, initialKey))
          Element[StaffGlyph](noteGroup.window, NoteGroupGlyph(noteSpellings))
        }

    lazy val layout: LayoutDescription[StaffGlyph] = LayoutDescription[StaffGlyph](
      layer.metres.iteratePositioned,
      RestGlyph(),
      Seq(
        StaffGlyphFamily.timeSignatureBuilder(layer.metres.metres),
        StaffGlyphFamily.keyBuilder(layer.keys.keys)
      )
    )

    def display: StaffGroup = {
      val trebleGlyphs = elementIterator(higherNoteNumbers(59))
      val bassGlyphs = elementIterator(lowerEqualNoteNumbers(59))

      val hasTreble = trebleGlyphs.nonEmpty
      val hasBass = bassGlyphs.nonEmpty

      val staffGroup = (hasTreble, hasBass) match {
        case (true, true) =>
          GrandStaff(
            NoteStaff(Treble, ElementLayout.layoutElements(trebleGlyphs, layout)),
            NoteStaff(Bass, ElementLayout.layoutElements(bassGlyphs, layout))
          )
        case (true, false) =>
          NoteStaff(Treble, ElementLayout.layoutElements(trebleGlyphs, layout))
        case (false, true) =>
          NoteStaff(Bass, ElementLayout.layoutElements(bassGlyphs, layout))
        case (false, false) =>
          NoteStaff(Bass, ElementLayout.layoutElements(Seq[Element[StaffGlyph]](), layout))
      }

      StaffGroup(staffGroup)
    }
  }
}

