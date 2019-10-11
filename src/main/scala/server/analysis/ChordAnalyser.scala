package server.analysis

import blackboard.KnowledgeSource
import music.symbols.{Chord, Note}
import music.analysis.{TwelveToneChordAnalysis, TwelveToneEqualTemprament}
import music.collection.Track
import music.primitives.{ChordFunctions, ChordRoot, PitchClass}

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
      chords.zipWithIndex.foreach { case (chord, index) =>
        val name = TwelveToneEqualTemprament.Chords.functionChordMapping.toMap.get(chord.get[ChordFunctions].get.functions)
        println(s"$pos (option $index): [${chord.get[ChordRoot].get.pc.value}] [$name]")
      }
    }

    possibleChords
      .groupBy { case (pos, _) => pos }
      .map { case (_, chords) => chords.head }
      .foldLeft(track) { case (acc, (pos, chord)) =>
        chord match {
          case c if c.nonEmpty => acc
            .updateSymbolTrack[Chord.type](
              _.addSymbolAt(pos, c.head)
            )
          case _ => acc
        }
      }
  }

}
