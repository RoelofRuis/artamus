package server.analysis

import music.analysis.TwelveTonePitchSpelling
import music.domain.track.Track
import server.analysis.blackboard.KnowledgeSource

class PitchSpellingAnalyser extends KnowledgeSource[Track] {

  override def canExecute(state: Track): Boolean = true

  override def execute(track: Track): Track = {
    val key = track.keys.initialKey

    val spelledNotes = track
      .notes
      .mapNotes(note => TwelveTonePitchSpelling.spellNote(note, key))

    val spelledChords = track
      .chords
      .mapChords(chord => TwelveTonePitchSpelling.spellChord(chord, key))

    track
      .overwriteNotes(spelledNotes)
      .writeChords(spelledChords)
  }

}
