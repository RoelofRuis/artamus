package server.analysis

import music.analysis.{TwelveToneChordSpelling, TwelveTonePitchSpelling}
import music.collection
import music.collection.SymbolTrack.Updater
import music.collection.Track
import music.primitives.Position
import music.symbols.{Chord, Key, Note}
import server.analysis.blackboard.KnowledgeSource

class PitchSpellingAnalyser extends KnowledgeSource[Track] {

  override def canExecute(state: Track): Boolean = true

  override def execute(track: Track): Track = {
    val key = track
      .getSymbolTrack[Key]
      .readFirstAt(Position.zero)
      .map(_.symbol)

    val noteSpellingUpdater = new Updater[Note] {
      override def apply(symbolTrack: collection.SymbolTrack[Note]): collection.SymbolTrack[Note] = {
        symbolTrack
          .readAllGrouped
          .flatMap(notes => TwelveTonePitchSpelling.spellNotes(notes, key))
          .foldRight(symbolTrack) { case (symbol, track) => track.updateSymbol(symbol) }
      }
    }

    val chordSpellingUpdater = new Updater[Chord] {
      override def apply(symbolTrack: collection.SymbolTrack[Chord]): collection.SymbolTrack[Chord] = {
        symbolTrack
          .readAll
          .map(chord => TwelveToneChordSpelling.spellChord(chord, key))
          .foldRight(symbolTrack) { case (symbol, track) => track.updateSymbol(symbol) }
      }
    }

    track
      .updateSymbolTrack(noteSpellingUpdater)
      .updateSymbolTrack(chordSpellingUpdater)
  }

}
