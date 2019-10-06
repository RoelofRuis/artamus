package server.analysis

import blackboard.{KnowledgeSource, OrderedSymbolMap}
import music.interpret.pitched.{ChordFinder, TwelveToneEqualTemprament}
import music.symbolic.pitch.PitchClass
import music.symbolic.temporal.Position

class ChordAnalyser extends KnowledgeSource[OrderedSymbolMap[Position]] {

  import Properties._

  override def canExecute(state: OrderedSymbolMap[Position]): Boolean = true
  override def execute(track: OrderedSymbolMap[Position]): OrderedSymbolMap[Position] = {
    val possibleChords = track.readAllWithPosition.map { case (position, notes) =>
      val pitches = notes.flatMap { props => props.getProperty[PitchClass] }
      val possibleChords = ChordFinder.findChords(pitches)
      (position, possibleChords)
    }

    println
    possibleChords.foreach { case (pos, chords) =>
      chords.foreach { chord =>
        val name = TwelveToneEqualTemprament.Chords.functionChordMapping.toMap.get(chord.functions.sorted)
        println(s"$pos: [${chord.root.value}] [$name]")
      }
    }
    track
  }

}
