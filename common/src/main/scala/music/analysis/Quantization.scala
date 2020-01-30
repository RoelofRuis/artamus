package music.analysis

import music.math.{HierarchicalClustering, Rational}
import music.math.HierarchicalClustering.Centroid
import music.math.temporal.{Duration, Position}

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

  val lengthList = Set(
    Rational(1, 8),
    Rational(1, 4),
    Rational(3, 8),
    Rational(1, 2),
    Rational(5, 8),
    Rational(3, 4),
    Rational(7, 8),
    Rational(1, 2)
  )

  // Choose a cluster C
  // For every L in lengthList
  //     n = C / L
  //     calc error (sum of distances of n*l to closest cluster)
  // Pick Smallest error (with most 'conventional' durations)
  private def determineCentroidMap(clusters: Seq[Centroid]): Map[Centroid, Duration] = {
    clusters match {
      case head :: tail =>
        val (resultMap, _) = lengthList.map { chosenLength =>
          val scalingFactor = head / chosenLength.toDouble
          val otherLengths = (lengthList - chosenLength)
            .map(r => (r.toDouble * scalingFactor, r))
            .zipWithIndex
            .map { case (length, index) => index -> length }
            .toMap
          calcError(tail, otherLengths, Map(head -> chosenLength), 0D)
        }.minBy { case (_, error) => error }

        resultMap.view.mapValues(r => Duration(r)).toMap
    }
  }

  type Error = Double

  @tailrec
  def calcError(
    centroids: List[Centroid],
    otherLengths: Map[Int, (Double, Rational)],
    resultMap: Map[Centroid, Rational],
    error: Error
  ): (Map[Centroid, Rational], Error) = {
    centroids match {
      case Nil => (resultMap, error)
      case head :: tail =>
        val (nextError, rational, index) = otherLengths
          .map { case (i, (scaled, rational)) => (Math.abs(scaled - head), rational, i) }
          .minBy { case (score, _, _) => score}

        calcError(
          tail,
          otherLengths.removed(index),
          resultMap.updated(head, rational),
          error + nextError,
        )
    }
  }

}
