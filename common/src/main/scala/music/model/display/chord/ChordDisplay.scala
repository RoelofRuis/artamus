package music.model.display.chord

import music.model.write.analysis.TwelveTonePitchSpelling
import math.temporal.{Position, Window}
import music.model.display.{Bars, NoteValues, StaffGroup}
import music.model.display.chord.ChordStaffGlyph.{ChordNameGlyph, ChordRestGlyph}
import music.model.write.Layers.ChordLayer

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

    private def read(window: Window): Iterator[ChordStaffGlyph] = {
      chords.nextOption match {
        case None => Iterator.empty

        case Some((nextWindow, nextChord)) =>
          val writeableChords = {
            // TODO: Use the 'active' key instead of initial key.
            val spelling = TwelveTonePitchSpelling.spellChord(nextChord, initialKey)
            val written = nextWindow.duration.asNoteValues match {
              case Nil => Seq()
              case head :: Nil =>
                ChordNameGlyph(head, spelling, nextChord.functions) :: Nil
              case head :: tail =>
                ChordNameGlyph(head, spelling, nextChord.functions) :: tail.map(ChordRestGlyph)
            }
            written.iterator
          }

          val rests = window.until(nextWindow) match {
            case None => Iterator.empty
            case Some(diff) =>
              layer
                .timeSignatures
                .fit(diff)
                .flatMap(_.duration.asNoteValues)
                .map(ChordRestGlyph)
                .iterator
          }

          rests ++ writeableChords ++ read(nextWindow)
      }
    }
  }

}
