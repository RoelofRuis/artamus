package domain.display.chord

import domain.write.analysis.TwelveTonePitchSpelling
import domain.math.temporal.{Position, Window}
import domain.display.{Bars, NoteValues, StaffGroup}
import domain.display.chord.ChordStaffGlyph.{ChordNameGlyph, ChordRestGlyph}
import domain.write.layers.ChordLayer

object ChordDisplay {

  import Bars._
  import NoteValues._

  implicit class ChordLayerDisplay(layer: ChordLayer) {

    private val chords = layer.chords.read
    private val initialKey = layer.keys.initialKey

    def getChords: StaffGroup = {
      // TODO: dynamic reading window
      val window = Window.instantAt(Position.ZERO)
      StaffGroup(ChordStaff(read(window)))
    }

    private def read(window: Window): Seq[ChordStaffGlyph] = {
      chords.nextOption match {
        case None => Seq.empty

        case Some((nextWindow, nextChord)) =>
          val writeableChords = {
            // TODO: Use the 'active' key instead of initial key.
            val spelling = TwelveTonePitchSpelling.spellChord(nextChord, initialKey)
            nextWindow.duration.asNoteValues match {
              case Nil => Seq()
              case head :: Nil =>
                ChordNameGlyph(head, spelling, nextChord.functions) :: Nil
              case head :: tail =>
                ChordNameGlyph(head, spelling, nextChord.functions) :: tail.map(ChordRestGlyph)
            }
          }

          val rests = window.until(nextWindow) match {
            case None => Seq.empty
            case Some(diff) =>
              layer
                .timeSignatures
                .fit(diff)
                .flatMap(_.duration.asNoteValues)
                .map(ChordRestGlyph)
          }

          rests ++ writeableChords ++ read(nextWindow)
      }
    }
  }

}
