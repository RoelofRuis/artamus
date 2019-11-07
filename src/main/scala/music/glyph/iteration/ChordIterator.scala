package music.glyph.iteration

import music.analysis.NoteValueConversion
import music.glyph
import music.glyph.{ChordGlyph, Glyph, RestGlyph}
import music.primitives._
import music.symbol.Chord
import music.symbol.collection.Track

class ChordIterator(track: Track) {

  import music.analysis.BarAnalysis._

  private val chords = track.read[Chord]()

  def iterate(start: Position): Iterator[Glyph] = {
    val window = Window.instantAt(start)
    read(window)
  }

  private def read(window: Window): Iterator[Glyph] = {
    chords.nextOption match {
      case None => Iterator.empty

      case Some(nextChord) =>
        val writeableChords = {
          val written = for {
            duration <- nextChord.symbol.duration
            spelling <- nextChord.symbol.rootSpelling
          } yield {
             NoteValueConversion.from(duration) match {
              case Nil => Seq()
              case head :: Nil =>
                ChordGlyph(head, spelling, nextChord.symbol.functions) :: Nil
              case head :: tail =>
                glyph.ChordGlyph(head, spelling, nextChord.symbol.functions) :: tail.map(RestGlyph(_, silent=true))
            }
          }
          written.map(_.iterator).getOrElse(Iterator())
        }

        val rests = window.until(nextChord.window) match {
          case None => Iterator.empty
          case Some(diff) =>
            track
              .fitToBars(diff)
              .flatMap(window => NoteValueConversion.from(window.duration))
              .map(glyph.RestGlyph(_, silent=true))
              .iterator
        }

        rests ++ writeableChords ++ read(nextChord.window)
    }
  }

}
