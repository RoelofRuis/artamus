package server.analysis

import blackboard.KnowledgeSource
import music.symbolic.pitch.PitchClass
import music.symbolic.temporal.Position
import server.domain.track.container.OrderedSymbolMap

import scala.collection.SortedMap

class PitchHistogramAnalyser extends KnowledgeSource[OrderedSymbolMap[Position]] {

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
      .readAll.flatMap(_.props.get[PitchClass])
      .foldRight(zero) { case (pc, acc) => acc.updated(pc.value, acc.get(pc.value).map(_ + 1L).get) }

    histogram.foreach { case (bin, count) =>
      println(s"$bin|: $count")
    }
    track
  }
}
