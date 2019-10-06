package server.analysis

import blackboard.{KnowledgeSource, OrderedSymbolMap}
import music.analysis.{TwelveToneChordAnalysis, TwelveToneEqualTemprament}
import music.symbolic.pitch.PitchClass
import music.symbolic.temporal.Position

class ChordAnalyser extends KnowledgeSource[OrderedSymbolMap[Position]] {

  import Properties._

  override def canExecute(state: OrderedSymbolMap[Position]): Boolean = true

  override def execute(track: OrderedSymbolMap[Position]): OrderedSymbolMap[Position] = {
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
