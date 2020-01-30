package music.analysis

import music.math.HierarchicalClustering.Centroid
import music.math.temporal.{Duration, Position}
import music.math.{HierarchicalClustering, Rational}

import scala.annotation.tailrec

object Quantization {

  /**
   * Quantizable data by the use of [[QuantizableOps]].
   *
   * @param input A list of milliseconds positions
   * @param instantaneousThreshold The millisecond threshold under which events would be perceived as occurring at the
   * same time. The default value is an assumption: based on 120 BPM a 64th note would be just above 31 ms inter onset
   * interval. 12 ms inter onset interval is the human limit. This, at least in theory, allows for measuring at least
   * 32th notes with reasonable precision.
   * @param interClusterDistance The maximum millisecond distance that time deltas might be considered to be part of the
   * same cluster. The default is put above the instantaneousThreshold but well below 250, the assumed duration of an
   * 8th note on 120 BPM.
   * @param wholeNoteDuration The number of milliseconds that a whole note takes. The default value is assuming 120 BPM.
   * @param consideredLengths The note lengths that will be considered by the algorithm.
   *
   * @param debug Whether printing of debug information is allowed. // TODO: implement!
   */
  final case class Quantizable(
    input: Seq[Long],
    instantaneousThreshold: Int = 30,
    interClusterDistance: Int = 100,
    wholeNoteDuration: Int = 2000,
    consideredLengths: Set[Rational] = Set(
      Rational(1, 8), Rational(1, 4), Rational(3, 8), Rational(1, 2),
      Rational(5, 8), Rational(3, 4), Rational(7, 8), Rational(1, 2)
    ),
    debug: Boolean = false
  )

  implicit class QuantizableOps(quantizable: Quantizable) {
    private implicit val doubleOrdering: Ordering[Double] = Ordering.Double.IeeeOrdering

    def toPositionList: List[Position] = {
      val differenceList = (quantizable.input drop 1)
        .lazyZip(quantizable.input)
        .map((`n+1`, n) => (`n+1` - n).toDouble)

      val nonInstantDifferences = differenceList.filter(_ > quantizable.instantaneousThreshold)

      if (nonInstantDifferences.isEmpty) List.fill(quantizable.input.size)(Position.ZERO)
      else {
        val clusterSettings = HierarchicalClustering.Settings(distanceThreshold = 100)
        val clusters = HierarchicalClustering.cluster(nonInstantDifferences, clusterSettings)

        val centroidMap = determineCentroidMap(clusters)

        pickDistances(differenceList, centroidMap)
      }
    }

    private def pickDistances(differenceList: Seq[Double], centroidMap: Map[Centroid, Rational]): List[Position] = {
      @tailrec
      def loop(data: Seq[Double], acc: List[Position]): List[Position] = {
        data match {
          case Nil => acc
          case head :: tail =>
            if (head < quantizable.instantaneousThreshold) loop(tail, acc :+ acc.last)
            else {
              val matchingLength = centroidMap.minBy { case (c, _) => Math.abs(c - head) }._2
              val nextPos: Position = acc.last + Duration(matchingLength)
              loop(tail, acc :+ nextPos)
            }
        }
      }
      loop(differenceList, List(Position.ZERO))
    }

    // Choose a cluster C
    // For every L in lengthList
    //     n = C / L
    //     calc error (sum of distances of n*l to closest cluster)
    // Pick Smallest error (with most 'conventional' durations)
    private def determineCentroidMap(clusters: Seq[Centroid]): Map[Centroid, Rational] = {
      clusters match {
        case head :: tail =>
          val mapsWithErrors = quantizable.consideredLengths.map { chosenLength =>
            val scalingFactor = head / chosenLength.toDouble
            val otherLengths = (quantizable.consideredLengths - chosenLength)
              .map(dur => (dur.toDouble * scalingFactor, dur))
              .zipWithIndex
              .map { case (length, index) => index -> length }
              .toMap
            calculateCentroidError(tail, otherLengths, Map(head -> chosenLength), 0D)
          }
          pickTempoOptimizedCentroidMap(mapsWithErrors)
      }
    }

    type Error = Double

    @tailrec
    private def calculateCentroidError(
      centroids: List[Centroid],
      otherLengths: Map[Int, (Double, Rational)],
      resultMap: Map[Centroid, Rational],
      error: Error
    ): (Error, Map[Centroid, Rational]) = {
      centroids match {
        case Nil => (error, resultMap)
        case head :: tail =>
          val (nextError, length, index) = otherLengths
            .map { case (i, (scaled, rational)) => (Math.abs(scaled - head), rational, i) }
            .minBy { case (score, _, _) => score}

          calculateCentroidError(
            tail,
            otherLengths.removed(index),
            resultMap.updated(head, length),
            error + nextError,
          )
      }
    }

    private def pickTempoOptimizedCentroidMap(centroidMaps: Set[(Error, Map[Centroid, Rational])]): Map[Centroid, Rational] = {
      val minimumError = centroidMaps.minBy(f => f._1)._1
      centroidMaps.toSeq.collect { case (error, map) if error == minimumError => map } match {
        case Seq(head) => head
        case seq =>
          seq.map { centroidMap =>
            val wholeNoteDurations = centroidMap.map { case (centroid, length) => centroid * length.reciprocal.toDouble }
            val averageWholeNote = wholeNoteDurations.sum / wholeNoteDurations.size
            val wholeNoteError = Math.abs(averageWholeNote - quantizable.wholeNoteDuration)
            (wholeNoteError, centroidMap)
          }.minBy { case (wholeNoteError, _) => wholeNoteError }._2
      }
    }
  }

}
