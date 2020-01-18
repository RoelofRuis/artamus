package music.analysis

import music.math.HierarchicalClustering.Centroid
import music.math.temporal.{Duration, Position}
import music.math.{HierarchicalClustering, Rational}

import scala.annotation.tailrec

object Quantization {

  private implicit val doubleOrdering: Ordering[Double] = Ordering.Double.IeeeOrdering

  def millisToPosition(input: Seq[Int]): List[Position] = {
    if (input.isEmpty) List()
    else if (input.size == 1) List(Position.ZERO)
    else determinePositions(input)
  }

  private def determinePositions(input: Seq[Int]): List[Position] = {
    val inputToDouble = input.map(_.toDouble)
    val differenceList = (inputToDouble drop 1)
      .lazyZip(inputToDouble)
      .map(_ - _)

    val clusterSettings = HierarchicalClustering.Settings(distanceThreshold = 100)
    val clusters = HierarchicalClustering.cluster(differenceList, clusterSettings)

    val centroidMap = determineCentroidMap(clusters)

    @tailrec
    def pickDistances(data: Seq[Double], acc: List[Position]): List[Position] = {
      data match {
        case Nil => acc
        case head :: tail =>
          val matchingDuration = centroidMap.minBy { case (c, _) => Math.abs(c - head) }._2
          val nextPos: Position = acc.last + matchingDuration
          pickDistances(tail, acc :+ nextPos)
      }
    }
    pickDistances(differenceList, List(Position.ZERO))
  }

  // TODO: improve this still terrible algorithm!
  private def determineCentroidMap(clusters: Seq[Centroid]): Map[Centroid, Duration] = {
    def closestTo(x: Double): Centroid = clusters.minBy(v => Math.abs(v - x))

    val quarterCentroid = closestTo(500) // just assumption! (quarter note based on 120BPM)
    if (clusters.sorted.indexOf(quarterCentroid) == 0) {
      val halfCentroid = closestTo(quarterCentroid * 2)
      Map(
        quarterCentroid -> Duration(Rational(1, 4)),
        halfCentroid -> Duration(Rational(1, 2))
      )
    } else {
      val eightCentroid = closestTo(quarterCentroid / 2)
      Map(
        eightCentroid -> Duration(Rational(1, 8)),
        quarterCentroid -> Duration(Rational(1, 4)),
      )
    }
  }

}
