package domain.display.layout

import domain.display.glyph.ChordStaffGlyphFamily.{ChordNameGlyph, ChordRestGlyph, ChordStaffGlyph}
import domain.display.glyph.StaffGlyphFamily.{NoteGroupGlyph, RestGlyph, StaffGlyph}
import domain.display.layout.ElementLayout.Element
import domain.display.staff.NoteStaff.{Bass, Treble}
import domain.display.staff._
import domain.primitives.{MidiNoteNumber, Note, NoteGroup}
import domain.write.analysis.TwelveTonePitchSpelling
import domain.write.layers.{ChordLayer, NoteLayer, RhythmLayer}

object DisplayableLayers {

  import MetrePositioning._

  implicit class DisplayableChordLayer(layer: ChordLayer) {
    private val initialKey = layer.keys.initialKey

    lazy val elementIterator: Seq[Element[ChordStaffGlyph]] = layer.chords.chords.map {
      case (_, (window, chord)) =>
        val spelling = TwelveTonePitchSpelling.spellChord(chord, initialKey)
        Element[ChordStaffGlyph](window, ChordNameGlyph(spelling, chord.functions))
    }.toSeq

    def display: StaffGroup = {
      StaffGroup(
        ChordStaff(
          ElementLayout.layoutElements(elementIterator, layer.metres.iteratePositioned, ChordRestGlyph())
        )
      )
    }
  }

  implicit class DisplayableRhythmLayer(layer: RhythmLayer) {
    lazy val elementIterator: Seq[Element[StaffGlyph]] = layer.voice.readGroupsList().map { noteGroup =>
      Element[StaffGlyph](noteGroup.window, NoteGroupGlyph(Seq()))
    }

    def display: StaffGroup = {
      StaffGroup(
        RhythmicStaff(
          ElementLayout.layoutElements(elementIterator, layer.metres.iteratePositioned, RestGlyph())
        )
      )
    }
  }

  implicit class DisplayableNoteLayer(layer: NoteLayer) {
    private val initialKey = layer.keys.initialKey

    import domain.write.analysis.TwelveToneTuning._

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

    def display: StaffGroup = {
      val trebleGlyphs = elementIterator(higherNoteNumbers(59))
      val bassGlyphs = elementIterator(lowerEqualNoteNumbers(59))

      val hasTreble = trebleGlyphs.nonEmpty
      val hasBass = bassGlyphs.nonEmpty

      val staffGroup = (hasTreble, hasBass) match {
        case (true, true) =>
          GrandStaff(
            NoteStaff(Treble, ElementLayout.layoutElements(trebleGlyphs, layer.metres.iteratePositioned, RestGlyph())),
            NoteStaff(Bass, ElementLayout.layoutElements(bassGlyphs, layer.metres.iteratePositioned, RestGlyph()))
          )
        case (true, false) =>
          NoteStaff(Treble, ElementLayout.layoutElements(trebleGlyphs, layer.metres.iteratePositioned, RestGlyph()))
        case (false, true) =>
          NoteStaff(Bass, ElementLayout.layoutElements(bassGlyphs, layer.metres.iteratePositioned, RestGlyph()))
        case (false, false) =>
          NoteStaff(Bass, ElementLayout.layoutElements(Seq[Element[StaffGlyph]](), layer.metres.iteratePositioned, RestGlyph()))
      }

      StaffGroup(staffGroup)
    }
  }
}

