package server.analysis

import music.analysis.{TwelveToneChordSpelling, TwelveTonePitchSpelling}
import music.primitives.Position
import music.symbol.collection.Track
import music.symbol.{Chord, Key, Note}
import server.analysis.blackboard.KnowledgeSource

class PitchSpellingAnalyser extends KnowledgeSource[Track] {

  override def canExecute(state: Track): Boolean = true

  override def execute(track: Track): Track = {
    val key = track
      .select[Key]
      .firstAt(Position.zero)
      .map(_.symbol)

    val spelledNotes = track
      .select[Note]
      .allGrouped
      .flatMap(notes => TwelveTonePitchSpelling.spellNotes(notes, key))

    val spelledChords = track
      .select[Chord]
      .all
      .map(chord => TwelveToneChordSpelling.spellChord(chord, key))

    track
      .updateAll(spelledNotes)
      .updateAll(spelledChords)
  }

}
