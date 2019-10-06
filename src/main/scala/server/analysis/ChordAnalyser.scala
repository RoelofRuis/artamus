package server.analysis

import blackboard.KnowledgeSource
import music.analysis.{TwelveToneChordAnalysis, TwelveToneEqualTemprament}
import music.symbolic.pitch.PitchClass
import server.domain.track.container.{ChordType, NoteType, SymbolProperties, Track}

class ChordAnalyser extends KnowledgeSource[Track] {

  override def canExecute(state: Track): Boolean = true

  override def execute(track: Track): Track = {
    val possibleChords = track.getSymbolTrack[NoteType.type].readAllWithPosition.map { case (position, notes) =>
      val pitches = notes.flatMap { symbol => symbol.props.get[PitchClass] }
      val possibleChords = TwelveToneChordAnalysis.findChords(pitches)
      (position, possibleChords)
    }

    // TODO: remove additional printing
    possibleChords.foreach { case (pos, chords) =>
      chords.zipWithIndex.foreach { case (chord, index) =>
        val name = TwelveToneEqualTemprament.Chords.functionChordMapping.toMap.get(chord.functions.sorted)
        println(s"$pos (option $index): [${chord.root.value}] [$name]")
      }
    }

    possibleChords
      .groupBy { case (pos, _) => pos }
      .map { case (_, chords) => chords.head }
      .foldLeft(track) { case (acc, (pos, chord)) =>
        chord match {
          case c if c.nonEmpty => acc
            .upsertSymbolTrack[ChordType.type](
              acc
                .getSymbolTrack[ChordType.type]
                .addSymbolAt(pos, SymbolProperties.empty.add(c.head))
            )
          case _ => acc
        }
      }
  }

}
