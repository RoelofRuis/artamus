package domain.record

import domain.math.HierarchicalClustering.Centroid
import domain.math.{HierarchicalClustering, Rational}
import domain.math.temporal.{Duration, Position}

import scala.annotation.tailrec

object Quantization {

  implicit class QuantizationOps(quantizer: Quantizer) {
    private implicit val doubleOrdering: Ordering[Double] = Ordering.Double.IeeeOrdering

    def quantize(input: Seq[MillisecondPosition]): List[Position] = {
      val differenceList = (input drop 1)
        .lazyZip(input)
        .map((`n+1`, n) => (`n+1`.v - n.v).toDouble)

      val nonInstantDifferences = differenceList.filter(_ > quantizer.instantaneousThreshold)

      if (nonInstantDifferences.isEmpty) List.fill(input.size)(Position.ZERO)
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
            if (head < quantizer.instantaneousThreshold) loop(tail, acc :+ acc.last)
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
        case head :: Nil =>
          val mapsWithErrors = quantizer.consideredLengths.map { chosenLength => (0D, Map(head -> chosenLength)) }
          pickTempoOptimizedCentroidMap(mapsWithErrors)

        case head :: tail =>
          val mapsWithErrors = quantizer.consideredLengths.map { chosenLength =>
            val scalingFactor = head / chosenLength.toDouble
            val otherLengths = (quantizer.consideredLengths - chosenLength)
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
          if (otherLengths.isEmpty) (error + (centroids.size * quantizer.unmatchedClusterPenalty), resultMap)
          else {
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
    }

    private def pickTempoOptimizedCentroidMap(centroidMaps: Set[(Error, Map[Centroid, Rational])]): Map[Centroid, Rational] = {
      val minimumError = centroidMaps.minBy(f => f._1)._1
      centroidMaps.toSeq.collect { case (error, map) if error == minimumError => map } match {
        case Seq(head) => head
        case seq =>
          seq.map { centroidMap =>
            val wholeNoteDurations = centroidMap.map { case (centroid, length) => centroid * length.reciprocal.toDouble }
            val averageWholeNote = wholeNoteDurations.sum / wholeNoteDurations.size
            val wholeNoteError = Math.abs(averageWholeNote - quantizer.wholeNoteDuration)
            (wholeNoteError, centroidMap)
          }.minBy { case (wholeNoteError, _) => wholeNoteError }._2
      }
    }
  }

}
