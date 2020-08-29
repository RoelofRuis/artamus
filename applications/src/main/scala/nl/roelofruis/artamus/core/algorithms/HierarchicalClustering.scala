package nl.roelofruis.artamus.core.algorithms

import scala.annotation.tailrec

object HierarchicalClustering {

  type Nodes = List[Int]
  type Centroid = Double
  type Score = Double
  type Cluster = (Centroid, Nodes)
  type Clusters = Map[Nodes, Centroid]

  private implicit val doubleOrdering: Ordering[Double] = Ordering.Double.IeeeOrdering

  final case class Settings(
    distanceThreshold: Double
  )

  def cluster(measurements: Seq[Double], settings: Settings): Seq[Centroid] = {
    @tailrec
    def combine(data: Clusters): Clusters = {
      if (data.size <= 1) data
      else {
        val best = score(data).minBy(_._1)
        if (best._1 > settings.distanceThreshold) data
        else {
          val newClusters = data
            .removed(best._3)
            .removed(best._4)
            .updated(best._3 ::: best._4, best._2)
          combine(newClusters)
        }
      }
    }
    combine(initialClusters(measurements)).values.toSeq
  }

  private def initialClusters(measurements: Seq[Double]): Clusters = {
    measurements
      .zipWithIndex
      .foldLeft(Map[Nodes, Centroid]()) {
        case (acc, (value, node)) => acc.updated(List(node), value)
      }
  }

  def score(l: Clusters): Seq[(Score, Centroid, Nodes, Nodes)] = {
    for {
      (x, idxX) <- l.toSeq.zipWithIndex
      (y, idxY) <- l.toSeq.zipWithIndex
      if idxX < idxY
    } yield {
      val numXNodes = x._1.size
      val numYNodes = y._1.size
      val newCentroid = ((x._2 * numXNodes) + (y._2 * numYNodes)) / (numXNodes + numYNodes)
      val score = Math.abs(x._2 - y._2)
      (score, newCentroid, x._1, y._1)
    }
  }

}
