package music.analysis

import music.math.HierarchicalClustering.Centroid
import music.math.{HierarchicalClustering, Rational}
import music.math.temporal.{Duration, Position}

import scala.annotation.tailrec

object Quantization {

  implicit val doubleOrdering: Ordering[Double] = Ordering.Double.IeeeOrdering

  def millisToPosition(input: Seq[Int]): List[Position] = {
    // Preprocess data
    val data = input.map(_.toDouble)
    val dataPoints = (data drop 1)
      .lazyZip(data)
      .map(_ - _)

    val clusterSettings = HierarchicalClustering.Settings(distanceThreshold = 100)
    val clusters = HierarchicalClustering.cluster(dataPoints, clusterSettings)

    // TODO: cluster to duration determination has to be expanded!
    val bpm = 120D
    val quarterDuration = (60 / bpm) * 1000
    val quarterCentroid = clusters.zipWithIndex.minBy { case (c, _) => Math.abs(c - quarterDuration) }._1
    val eightCentroid = clusters.zipWithIndex.minBy { case (c, _) => Math.abs(c - (quarterCentroid / 2)) }._1

    val centroidMap = Map[Centroid, Duration](
      quarterCentroid -> Duration(Rational(1, 4)),
      eightCentroid -> Duration(Rational(1, 8))
    )

    @tailrec
    def pickDistances(data: Seq[Double], acc: List[Position]): List[Position] = {
      data match {
        case Nil => acc
        case head :: tail =>
          val bestCentroid = centroidMap.keys.minBy { c => Math.abs(c - head) }
          val nextPos: Position = acc.last + centroidMap(bestCentroid)
          pickDistances(tail, acc :+ nextPos)
      }
    }
    pickDistances(data, List(Position.ZERO))
  }

}
