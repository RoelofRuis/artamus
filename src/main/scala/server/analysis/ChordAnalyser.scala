package server.analysis

import blackboard.{KnowledgeSource, OrderedSymbolMap}
import music.analysis.TwelveToneEqualTemprament
import music.symbolic.pitch.{Chord, PitchClass}
import music.symbolic.temporal.Position

class ChordAnalyser extends KnowledgeSource[OrderedSymbolMap[Position]] {

  import Properties._

  override def canExecute(state: OrderedSymbolMap[Position]): Boolean = true

  override def execute(track: OrderedSymbolMap[Position]): OrderedSymbolMap[Position] = {
    val possibleChords = track.readAllWithPosition.map { case (position, notes) =>
      val pitches = notes.flatMap { props => props.getProperty[PitchClass] }
      val possibleChords = findChords(pitches)
      (position, possibleChords)
    }

    println
    possibleChords.foreach { case (pos, chords) =>
      chords.zipWithIndex.foreach { case (chord, index) =>
        val name = TwelveToneEqualTemprament.Chords.functionChordMapping.toMap.get(chord.functions.sorted)
        println(s"$pos (option $index): [${chord.root.value}] [$name]")
      }
    }
    track
  }

  import music.analysis.Analysis._
  import TwelveToneEqualTemprament._

  private def findChords(set: Seq[PitchClass]): Seq[Chord] = {
    tuning.pcs.flatMap{ root =>
      Interpretation.allOf(set)
        .expand(pc => tuning.possibleIntervals(root, pc))
        .expand(tuning.possibleFunctions)
        .filter(tuning.functionsToName(_).nonEmpty)
        .data.map(functions => Chord(root, functions))
    }
  }

}
