package server.analysis

import music.analysis.TwelveToneChordAnalysis
import music.collection.Track
import music.symbols.{Chord, Note}
import server.analysis.blackboard.KnowledgeSource

class ChordAnalyser extends KnowledgeSource[Track] {

  override def canExecute(state: Track): Boolean = true

  override def execute(track: Track): Track = {
    val possibleChords = track.getSymbolTrack[Note].readAllGrouped.map { notes =>
      val pitches = notes.map { note => note.symbol.pitchClass }
      val dur = notes.map { note => note.symbol.duration }.max
      val possibleChords = TwelveToneChordAnalysis.findChords(pitches).map(_.withDuration(dur))
      (notes.head.position, possibleChords)
    }

    possibleChords
      .groupBy { case (pos, _) => pos }
      .map { case (_, chords) => chords.head }
      .foldLeft(track) { case (acc, (pos, chord)) =>
        chord match {
          case c if c.nonEmpty => acc
            .updateSymbolTrack[Chord](
              _.addSymbolAt(pos, c.head) // TODO: determine best option instead of picking head
            )
          case _ => acc
        }
      }
  }

}
