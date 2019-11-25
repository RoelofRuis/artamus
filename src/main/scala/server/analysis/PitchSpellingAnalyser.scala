package server.analysis

import music.analysis.TwelveTonePitchSpelling
import music.domain.track.Track
import music.domain.track.symbol.{Chord, Note}
import server.analysis.blackboard.KnowledgeSource

class PitchSpellingAnalyser extends KnowledgeSource[Track] {

  override def canExecute(state: Track): Boolean = true

  override def execute(track: Track): Track = {
    val key = track.keys.initialKey

    val spelledNotes = track
      .readGrouped[Note]()
      .flatMap(notes => TwelveTonePitchSpelling.spellNotes(notes, key))

    val spelledChords = track
      .read[Chord]()
      .map(chord => TwelveTonePitchSpelling.spellChord(chord, key))

    track
      .updateAll(spelledNotes)
      .updateAll(spelledChords)
  }

}
