package music.model.display.chord

import music.analysis.TwelveTonePitchSpelling
import music.math.temporal.{Position, Window}
import music.model.display.{Bars, NoteValues}
import music.model.display.chord.ChordStaffGlyph.{ChordNameGlyph, ChordRestGlyph}
import music.model.write.Track

object ChordDisplay {

  import Bars._
  import NoteValues._

  implicit class ChordStaffDisplay(track: Track) {

    private val chords = track.chords.read
    private val initialKey = track.keys.initialKey

    def getChordStaff: ChordStaff = {
      // TODO: dynamic reading window
      val window = Window.instantAt(Position.ZERO)
      ChordStaff(read(window))
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
              track
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
