package server.analysis

import blackboard.KnowledgeSource
import music.analysis.{TwelveToneChordAnalysis, TwelveToneEqualTemprament}
import music.Symbols.{Chord, Note}
import music.collection.{SymbolProperties, Track}
import music.primitives.PitchClass

class ChordAnalyser extends KnowledgeSource[Track] {

  override def canExecute(state: Track): Boolean = true

  override def execute(track: Track): Track = {
    val possibleChords = track.getSymbolTrack[Note.type].readAllWithPosition.map { case (position, notes) =>
      val pitches = notes.flatMap { symbol => symbol.props.get[PitchClass] }
      val possibleChords = TwelveToneChordAnalysis.findChords(pitches)
      (position, possibleChords)
    }

    // TODO: remove additional printing
    possibleChords.foreach { case (pos, chords) =>
      chords.zipWithIndex.foreach { case ((chordRoot, chordFunctions), index) =>
        val name = TwelveToneEqualTemprament.Chords.functionChordMapping.toMap.get(chordFunctions.functions.sorted)
        println(s"$pos (option $index): [${chordRoot.pc.value}] [$name]")
      }
    }

    possibleChords
      .groupBy { case (pos, _) => pos }
      .map { case (_, chords) => chords.head }
      .foldLeft(track) { case (acc, (pos, chord)) =>
        chord match {
          case c if c.nonEmpty => acc
            .updateSymbolTrack[Chord.type](
              _.addSymbolAt(pos, SymbolProperties.empty.add(c.head._1).add(c.head._2))
            )
          case _ => acc
        }
      }
  }

}
