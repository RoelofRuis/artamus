package server.analysis

import music.analysis.TwelveToneChordAnalysis
import music.symbol.Note
import music.symbol.collection.Track
import server.analysis.blackboard.KnowledgeSource

class ChordAnalyser extends KnowledgeSource[Track] {

  override def canExecute(state: Track): Boolean = true

  override def execute(track: Track): Track = {
    val possibleChords = track.read[Note].allGrouped.flatMap { notes =>
      val pitches = notes.map { note => note.symbol.pitchClass }
      val dur = notes.map { note => note.duration }.max
      val possibleChords = TwelveToneChordAnalysis.findChords(pitches).map(_.withDuration(dur))

      if (possibleChords.nonEmpty) Some(notes.head.position, possibleChords.head) // TODO: determine best option instead of picking head
      else None
    }

    track.createAll(possibleChords)
  }

}
