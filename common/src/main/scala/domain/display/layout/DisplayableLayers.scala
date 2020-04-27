package domain.display.layout

import domain.display.glyph.ChordStaffGlyphFamily.{ChordNameGlyph, ChordRestGlyph, ChordStaffGlyph}
import domain.display.glyph.StaffGlyphFamily.{NoteGroupGlyph, RestGlyph, StaffGlyph}
import domain.display.staff.NoteStaff.{Bass, Treble}
import domain.display.staff._
import domain.primitives.{MidiNoteNumber, Note, NoteGroup}
import domain.write.analysis.TwelveTonePitchSpelling
import domain.write.layers.{ChordLayer, NoteLayer, RhythmLayer}

object DisplayableLayers {

  implicit class DisplayableChordLayer(layer: ChordLayer) {
    private val initialKey = layer.keys.initialKey

    lazy val elementIterator: Seq[Windowed[ChordStaffGlyph]] = layer.chords.chords.map {
      case (_, (window, chord)) =>
        val spelling = TwelveTonePitchSpelling.spellChord(chord, initialKey)
        Windowed[ChordStaffGlyph](window, ChordNameGlyph(spelling, chord.functions))
    }.toSeq

    def display: StaffGroup = {
      StaffGroup(
        ChordStaff(
          LayerLayout.layoutGlyphs(elementIterator, ChordRestGlyph(), Bars())
        )
      )
    }
  }

  implicit class DisplayableRhythmLayer(layer: RhythmLayer) {
    lazy val elementIterator: Seq[Windowed[StaffGlyph]] = layer.voice.readGroupsList().map { noteGroup =>
      Windowed[StaffGlyph](noteGroup.window, NoteGroupGlyph(Seq()))
    }

    def display: StaffGroup = {
      StaffGroup(
        RhythmicStaff(
          LayerLayout.layoutGlyphs(elementIterator, RestGlyph(), Bars())
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

    def elementIterator(inclusion: InclusionStrategy): Seq[Windowed[StaffGlyph]] =
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
          Windowed[StaffGlyph](noteGroup.window, NoteGroupGlyph(noteSpellings))
        }

    def display: StaffGroup = {
      val trebleGlyphs = elementIterator(higherNoteNumbers(59))
      val bassGlyphs = elementIterator(lowerEqualNoteNumbers(59))

      val hasTreble = trebleGlyphs.nonEmpty
      val hasBass = bassGlyphs.nonEmpty

      val staffGroup = (hasTreble, hasBass) match {
        case (true, true) =>
          GrandStaff(
            NoteStaff(Treble, LayerLayout.layoutGlyphs(trebleGlyphs, RestGlyph(), Bars())),
            NoteStaff(Bass, LayerLayout.layoutGlyphs(bassGlyphs, RestGlyph(), Bars()))
          )
        case (true, false) =>
          NoteStaff(Treble, LayerLayout.layoutGlyphs(trebleGlyphs, RestGlyph(), Bars()))
        case (false, true) =>
          NoteStaff(Bass, LayerLayout.layoutGlyphs(bassGlyphs, RestGlyph(), Bars()))
        case (false, false) =>
          NoteStaff(Bass, LayerLayout.layoutGlyphs(Seq[Windowed[StaffGlyph]](), RestGlyph(), Bars()))
      }

      StaffGroup(staffGroup)
    }
  }
}

