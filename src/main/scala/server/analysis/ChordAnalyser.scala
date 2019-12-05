package server.analysis

import music.analysis.TwelveToneChordAnalysis
import music.domain.track.{Chords, Track}
import server.analysis.blackboard.KnowledgeSource

class ChordAnalyser extends KnowledgeSource[Track] {

  override def canExecute(state: Track): Boolean = true

  override def execute(track: Track): Track = {
    val analysedChords = track
      .notes
      .readGroups
      .flatMap { noteGroup =>
        val pitches = noteGroup.notes.map { note => note.pitchClass }
        val possibleChords = TwelveToneChordAnalysis.findChords(pitches)

        if (possibleChords.nonEmpty) Some(noteGroup.window, possibleChords.head) // TODO: determine best option instead of picking head
        else None
      }
      .foldRight(Chords()){ case ((window, chord), acc) => acc.writeChord(window, chord) }

    track.writeChords(analysedChords)
  }

}
