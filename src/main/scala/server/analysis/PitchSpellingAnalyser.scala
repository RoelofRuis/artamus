package server.analysis

import music.analysis.TwelveTonePitchSpelling
import music.primitives.Position
import music.symbol.collection.Track
import music.symbol.{Chord, Key, Note}
import server.analysis.blackboard.KnowledgeSource

class PitchSpellingAnalyser extends KnowledgeSource[Track] {

  override def canExecute(state: Track): Boolean = true

  override def execute(track: Track): Track = {
    val key = track
      .read[Key]
      .firstAt(Position.zero)
      .map(_.symbol)

    val spelledNotes = track
      .read[Note]
      .allGrouped
      .flatMap(notes => TwelveTonePitchSpelling.spellNotes(notes, key))

    val spelledChords = track
      .read[Chord]
      .all
      .map(chord => TwelveTonePitchSpelling.spellChord(chord, key))

    track
      .updateAll(spelledNotes)
      .updateAll(spelledChords)
  }

}
