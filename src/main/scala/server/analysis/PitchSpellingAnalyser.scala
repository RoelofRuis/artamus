package server.analysis

import music.analysis.TwelveTonePitchSpelling
import music.symbol.collection.Track
import music.symbol.{Chord, Key, Note}
import server.analysis.blackboard.KnowledgeSource

class PitchSpellingAnalyser extends KnowledgeSource[Track] {

  override def canExecute(state: Track): Boolean = true

  override def execute(track: Track): Track = {
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
