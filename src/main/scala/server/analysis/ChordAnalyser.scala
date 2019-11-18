package server.analysis

import music.analysis.TwelveToneChordAnalysis
import music.domain.track.Track2
import music.symbol.{Chord, Note}
import server.analysis.blackboard.KnowledgeSource

class ChordAnalyser extends KnowledgeSource[Track2] {

  override def canExecute(state: Track2): Boolean = true

  override def execute(track: Track2): Track2 = {
    val possibleChords = track.readGrouped[Note]().flatMap { notes =>
      val pitches = notes.map { note => note.symbol.pitchClass }
      val possibleChords = TwelveToneChordAnalysis.findChords(pitches)

      if (possibleChords.nonEmpty) Some(notes.head.window, possibleChords.head) // TODO: determine best option instead of picking head
      else None
    }

    track
      .deleteAll[Chord]()
      .createAll(possibleChords)
  }

}
