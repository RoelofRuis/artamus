package music.display.iteration

import music.analysis.{NoteValueConversion, TwelveTonePitchSpelling}
import music.display.{ChordGlyph, Glyph, RestGlyph}
import music.domain.track.Track
import music.math.temporal.{Position, Window}

private[display] class ChordIterator(track: Track) {

  import music.display.neww.Bars._

  private val chords = track.chords.read
  private val initialKey = track.keys.initialKey

  def iterate(start: Position): Iterator[Glyph] = {
    val window = Window.instantAt(start)
    read(window)
  }

  private def read(window: Window): Iterator[Glyph] = {
    chords.nextOption match {
      case None => Iterator.empty

      case Some((nextWindow, nextChord)) =>
        val writeableChords = {
          // TODO: Use the 'active' key instead of initial key.
          val spelling = TwelveTonePitchSpelling.spellChord(nextChord, initialKey)
          val written = NoteValueConversion.from(nextWindow.duration) match {
            case Nil => Seq()
            case head :: Nil =>
              ChordGlyph(head, spelling, nextChord.functions) :: Nil
            case head :: tail =>
              ChordGlyph(head, spelling, nextChord.functions) :: tail.map(RestGlyph(_, silent=true))
          }
          written.iterator
        }

        val rests = window.until(nextWindow) match {
          case None => Iterator.empty
          case Some(diff) =>
            track
              .timeSignatures
              .fit(diff)
              .flatMap(window => NoteValueConversion.from(window.duration))
              .map(RestGlyph(_, silent=true))
              .iterator
        }

        rests ++ writeableChords ++ read(nextWindow)
    }
  }

}
