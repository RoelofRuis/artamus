package server.analysis

import music.analysis.TwelveToneChordAnalysis
import music.symbol.{Chord, Note}
import music.symbol.collection.Track
import server.analysis.blackboard.KnowledgeSource

class ChordAnalyser extends KnowledgeSource[Track] {

  override def canExecute(state: Track): Boolean = true

  override def execute(track: Track): Track = {
    val possibleChords = track.readGrouped[Note]().flatMap { notes =>
      val pitches = notes.map { note => note.symbol.pitchClass }
      val dur = notes.map { note => note.symbol.duration }.max
      val possibleChords = TwelveToneChordAnalysis.findChords(pitches).map(_.withDuration(dur))

      if (possibleChords.nonEmpty) Some(notes.head.window, possibleChords.head) // TODO: determine best option instead of picking head
      else None
    }

    track
      .deleteAll[Chord]()
      .createAll(possibleChords)
  }

}
