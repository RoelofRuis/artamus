package music.analysis

import music.math.HierarchicalClustering.Centroid
import music.math.temporal.{Duration, Position}
import music.math.{HierarchicalClustering, Rational}

import scala.annotation.tailrec

object Quantization {

  /* This is an assumption: based on 120 BPM a 64th note would be just above 31 ms inter onset interval.
   * 12 ms inter onset interval is human limit
   * If MIDI real time measurement gets more accurate, this could be reduced. */
  private val instantaneousThreshold = 30

  private implicit val doubleOrdering: Ordering[Double] = Ordering.Double.IeeeOrdering

  def millisToPosition(input: Seq[Long]): List[Position] = {
    val differenceList = (input drop 1)
      .lazyZip(input)
      .map((`n+1`, n) => (`n+1` - n).toDouble)

    val nonInstantDifferences = differenceList.filter(_ > instantaneousThreshold)

    if (nonInstantDifferences.isEmpty) List.fill(input.size)(Position.ZERO)
    else {
      val clusterSettings = HierarchicalClustering.Settings(distanceThreshold = 100)
      val clusters = HierarchicalClustering.cluster(nonInstantDifferences, clusterSettings)

      val centroidMap = determineCentroidMap(clusters)

      pickDistances(differenceList, centroidMap)
    }
  }

  private def pickDistances(differenceList: Seq[Double], centroidMap: Map[Centroid, Duration]): List[Position] = {
    @tailrec
    def loop(data: Seq[Double], acc: List[Position]): List[Position] = {
      data match {
        case Nil => acc
        case head :: tail =>
          if (head < instantaneousThreshold) loop(tail, acc :+ acc.last)
          else {
            val matchingDuration = centroidMap.minBy { case (c, _) => Math.abs(c - head) }._2
            val nextPos: Position = acc.last + matchingDuration
            loop(tail, acc :+ nextPos)
          }
      }
    }
    loop(differenceList, List(Position.ZERO))
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
