package server.analysis

import music.analysis.TwelveTonePitchSpelling
import music.domain.track.Track2
import music.domain.track.symbol.{Chord, Key, Note}
import server.analysis.blackboard.KnowledgeSource

class PitchSpellingAnalyser extends KnowledgeSource[Track2] {

  override def canExecute(state: Track2): Boolean = true

  override def execute(track: Track2): Track2 = {
    val key = track
      .read[Key]()
      .headOption
      .map(_.symbol)

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
