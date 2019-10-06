package server.analysis

import blackboard.KnowledgeSource
import music.analysis.{TwelveToneChordAnalysis, TwelveToneEqualTemprament}
import music.symbolic.pitch.PitchClass
import server.domain.track.container.{NoteType, Track}

class ChordAnalyser extends KnowledgeSource[Track] {

  override def canExecute(state: Track): Boolean = true

  override def execute(track: Track): Track = {
    val possibleChords = track.getSymbolTrack[NoteType.type].readAllWithPosition.map { case (position, notes) =>
      val pitches = notes.flatMap { symbol => symbol.props.get[PitchClass] }
      val possibleChords = TwelveToneChordAnalysis.findChords(pitches)
      (position, possibleChords)
    }

    possibleChords.foreach { case (pos, chords) =>
      chords.zipWithIndex.foreach { case (chord, index) =>
        val name = TwelveToneEqualTemprament.Chords.functionChordMapping.toMap.get(chord.functions.sorted)
        println(s"$pos (option $index): [${chord.root.value}] [$name]")
      }
    }
    track
  }

}
