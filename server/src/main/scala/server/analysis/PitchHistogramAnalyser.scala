package server.analysis

import domain.write.Layers.NoteLayer
import domain.write.Track
import server.analysis.blackboard.KnowledgeSource

import scala.collection.immutable.SortedMap

class PitchHistogramAnalyser extends KnowledgeSource[Track] {

  override def execute(track: Track): Track = {
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
      .layers
      .collectFirst { case noteLayer: NoteLayer =>
        noteLayer
          .notes
          .read()
      }
      .getOrElse(Iterator())
      .map(_.pitchClass)
      .foldRight(zero) { case (pc, acc) => acc.updated(pc.value, acc.get(pc.value).map(_ + 1L).get) }

    histogram.foreach { case (bin, count) =>
      println(s"$bin|: $count")
    }
    track
  }
}
