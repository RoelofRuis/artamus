package server.analysis

import blackboard.OrderedSymbolMap
import music.interpret.pitched.{ChordFinder, NaivePitchSpelling, TwelveToneEqualTemprament}
import music.symbolic.pitch.{PitchClass, SpelledNote}
import music.symbolic.symbol.{Key, TimeSignature}
import music.symbolic.temporal.Position
import server.rendering.LilypondFile

import scala.collection.SortedMap

/*
TODO: Over time this has to become the blackboard strategy
*/
class RenderingAnalysis() {

  import Properties._

  // Transformatief
  // A => A

  // Einde
  // A => B

  def toLilypondFile(key: String, trackState: OrderedSymbolMap[Position]): LilypondFile = {
    analyseChords(trackState)
    analysePitchHistogram(trackState)
    analyseMelody(trackState)
  }

  def analyseChords(track:  OrderedSymbolMap[Position]): Unit = {
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
  }

  def analyseMelody(track: OrderedSymbolMap[Position]): LilypondFile = {
    val stackedNotes: Seq[Seq[SpelledNote]] =
      track.readAllWithPosition
        .map { case (_, symbols) =>
          symbols.flatMap { symbol => NaivePitchSpelling.spell(symbol) }
        }

    LilypondFile(
      stackedNotes,
      track.readAt(Position.zero).map(_.getProperty[TimeSignature]).head,
      track.readAt(Position.zero).map(_.getProperty[Key]).head
    )
  }

  def analysePitchHistogram(track: OrderedSymbolMap[Position]): Unit = {
    val zero = SortedMap(
      0 -> 0L,
      1 -> 0L,
      2 -> 0L,
      3 -> 0L,
      4 -> 0L,
      5 -> 0L,
      6 -> 0L,
      7 -> 0L,
      8 -> 0L,
      9 -> 0L,
      10 -> 0L,
      11 -> 0L
    )

    val histogram = track
      .readAll.flatMap(_.getProperty[PitchClass])
      .foldRight(zero) { case (pc, acc) => acc.updated(pc.value, acc.get(pc.value).map(_ + 1L).get) }

    histogram.foreach { case (bin, count) =>
      println(s"$bin|: $count")
    }
  }

}
