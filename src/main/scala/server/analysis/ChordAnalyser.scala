package server.analysis

import music.analysis.TwelveToneChordAnalysis
import music.symbol.collection.SymbolTrack.Updater
import music.symbol.collection.Track
import music.symbol.{Chord, Note, collection}
import server.analysis.blackboard.KnowledgeSource

class ChordAnalyser extends KnowledgeSource[Track] {

  override def canExecute(state: Track): Boolean = true

  override def execute(track: Track): Track = {
    val possibleChords = track.getSymbolTrack[Note].readAllGrouped.flatMap { notes =>
      val pitches = notes.map { note => note.symbol.pitchClass }
      val dur = notes.map { note => note.symbol.duration }.max
      val possibleChords = TwelveToneChordAnalysis.findChords(pitches).map(_.withDuration(dur))

      if (possibleChords.nonEmpty) Some((notes.head.position), possibleChords.head) // TODO: determine best option instead of picking head
      else None
    }

    val updateChords = new Updater[Chord] {
      override def apply(symbolTrack: collection.SymbolTrack[Chord]): collection.SymbolTrack[Chord] = {
        possibleChords.foldLeft(symbolTrack) {
          case (acc, (pos, chord)) => acc.addSymbolAt(pos, chord)
        }
      }
    }

    track.updateSymbolTrack(updateChords)
  }

}
