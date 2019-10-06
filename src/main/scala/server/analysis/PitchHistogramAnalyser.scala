package server.analysis

import blackboard.{KnowledgeSource, OrderedSymbolMap}
import music.symbolic.pitch.PitchClass
import music.symbolic.temporal.Position

import scala.collection.SortedMap

class PitchHistogramAnalyser extends KnowledgeSource[OrderedSymbolMap[Position]] {
  import Properties._

  override def canExecute(state: OrderedSymbolMap[Position]): Boolean = true

  override def execute(track: OrderedSymbolMap[Position]): OrderedSymbolMap[Position] = {
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
    track
  }
}
