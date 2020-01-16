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
    else if (input.size == 2) List(Position.ZERO, Position(Rational(1, 4)))
    else determinePositions(input)
  }

  private def determinePositions(input: Seq[Int]): List[Position] = {
    // Preprocess data
    val inputToDouble = input.map(_.toDouble)
    val differenceList = (inputToDouble drop 1)
      .lazyZip(inputToDouble)
      .map(_ - _)

    val clusterSettings = HierarchicalClustering.Settings(distanceThreshold = 100)
    val clusters = HierarchicalClustering.cluster(differenceList, clusterSettings)

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
          val matchingDuration = centroidMap.minBy { case (c, _) => Math.abs(c - head) }._2
          val nextPos: Position = acc.last + matchingDuration
          pickDistances(tail, acc :+ nextPos)
      }
    }
    pickDistances(differenceList, List(Position.ZERO))
  }

}
