package server.analysis

import music.model.write.analysis.TwelveToneChordAnalysis
import music.model.write.Layers.{ChordLayer, NoteLayer}
import music.model.write.{Chords, Track}
import server.analysis.blackboard.KnowledgeSource

class ChordAnalyser extends KnowledgeSource[Track] {

  override def execute(track: Track): Track = {
    val chordLayer = track.layers.collectFirst { case noteLayer: NoteLayer =>
      val chords = noteLayer
        .notes
        .readGroups()
        .flatMap { noteGroup =>
          val pitches = noteGroup.notes.map { note => note.pitchClass }
          val possibleChords = TwelveToneChordAnalysis.findChords(pitches)

          if (possibleChords.nonEmpty) Some(noteGroup.window, possibleChords.head) // TODO: determine best option instead of picking head
          else None
        }
        .foldRight(Chords()) { case ((window, chord), acc) => acc.writeChord(window, chord) }

      ChordLayer(noteLayer.timeSignatures, noteLayer.keys, chords)
    }

    chordLayer match {
      case Some(layer) => track.addLayer(layer)
      case None => track
    }
  }

}
