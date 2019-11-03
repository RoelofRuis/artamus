package server.interpret.lilypond

import music.primitives._
import music.symbol.{Chord, TimeSignature}
import music.symbol.collection.Track

class ChordIterator(track: Track) {

  import music.analysis.BarAnalysis._
  import server.interpret.lilypond.LilypondFormat._

  private val timeSignatures = track.read[TimeSignature]
  private val chords = track.read[Chord]

  def iterate(start: Position): Iterator[String] = {
    val window = Window(start, start)
    read(window, readFrom = false)
  }

  private def read(window: Window, readFrom: Boolean = true): Iterator[String] = {
    val element = if (readFrom) chords.firstNext(window.start) else chords.firstAt(window.start)
    element match {
      case None =>
        if (readFrom) Iterator.empty
        else read(window)

      case Some(nextChord) =>
        val writeableChords = {
          val written = for {
            duration <- nextChord.symbol.duration
            spelling <- nextChord.symbol.rootSpelling
          } yield {
             WriteableDuration.from(duration) match {
              case Nil => Seq()
              case head :: Nil =>
                WriteableChord(head, spelling, nextChord.symbol.functions).toLilypond :: Nil
              case head :: tail =>
                WriteableChord(head, spelling, nextChord.symbol.functions).toLilypond :: tail.map(restToLilypond(_, silent=true))
            }
          }
          written.map(_.toIterator).getOrElse(Iterator())
        }

        val rests = window.until(nextChord.window) match {
          case None => Iterator.empty
          case Some(diff) =>
            timeSignatures
              .fitToBars(diff)
              .flatMap(window => WriteableDuration.from(window.duration))
              .map(duration => restToLilypond(duration, silent=true))
              .toIterator
        }

        rests ++ writeableChords ++ read(nextChord.window)
    }
  }

}
