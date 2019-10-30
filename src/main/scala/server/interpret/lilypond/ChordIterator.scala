package server.interpret.lilypond

import music.primitives._
import music.symbol.{Chord, TimeSignature}
import music.symbol.collection.Track

class ChordIterator(track: Track) {

  import music.analysis.BarAnalysis._
  import server.interpret.lilypond.LilypondFormat._

  private val timeSignatures = track.read[TimeSignature]
  private val chords = track.read[Chord]

  case class PositionIndicator(window: Window, isFirst: Boolean)

  def iterate(start: Position): Iterator[String] = {
    val window = Window(start, start)
    val positionIndicator = PositionIndicator(window, isFirst=true)

    read(positionIndicator)
  }

  private def read(pos: PositionIndicator): Iterator[String] = {
    val element = if (pos.isFirst) chords.firstAt(pos.window.start) else chords.firstNext(pos.window.start)
    element match {
      case None =>
        if (pos.isFirst) read(PositionIndicator(pos.window, isFirst=false))
        else Iterator.empty

      case Some(nextChord) =>
        val rests = pos.window.until(nextChord.window) match {
          case None => Iterator.empty
          case Some(diff) =>
            timeSignatures
              .fitToBars(diff)
              .map(window => restToLilypond(window.duration, silent=true))
              .toIterator
        }

        nextChord.symbol.toLilypond match {
          case None => rests ++ read(PositionIndicator(nextChord.window, isFirst=false))
          case Some(lilyString) => rests ++ Iterator(lilyString) ++ read(PositionIndicator(nextChord.window, isFirst=false))
        }
    }
  }

}
