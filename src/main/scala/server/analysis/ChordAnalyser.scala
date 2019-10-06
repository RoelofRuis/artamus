package server.analysis

import blackboard.KnowledgeSource
import music.analysis.{TwelveToneChordAnalysis, TwelveToneEqualTemprament}
import music.symbolic.pitch.PitchClass
import music.symbolic.temporal.Position
import server.domain.track.container.SymbolTrack

class ChordAnalyser extends KnowledgeSource[SymbolTrack[Position]] {

  override def canExecute(state: SymbolTrack[Position]): Boolean = true

  override def execute(track: SymbolTrack[Position]): SymbolTrack[Position] = {
    val possibleChords = track.readAllWithPosition.map { case (position, notes) =>
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
