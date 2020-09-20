package nl.roelofruis.artamus.core.common.algorithms

import scala.collection.mutable
import scala.reflect.ClassTag

object GraphSearch {

  private final case class Solution(path: Array[Int], score: Int) extends Ordered[Solution] {
    override def compare(that: Solution): Int = score.compare(that.score)
  }

  def bestFirst[S, N : ClassTag](
    maxSolutionsToCheck: Int,
    findNodes: S => Seq[N],
    scoreTransition: (N, N) => Option[Int]
  )(stateSequence: Seq[S]): Option[Array[N]] = {
    var best: Option[Solution] = None
    var checkedSolutions: Int = 0

    val inputStates = new mutable.PriorityQueue[Solution]()
    val hypotheses: Array[Array[(N, Int)]] = stateSequence.map { state => findNodes(state).zipWithIndex.toArray }.toArray

    hypotheses(0).foreach { case (_, i) => inputStates.enqueue(Solution(Array(i), 0)) }

    while (inputStates.nonEmpty && checkedSolutions < maxSolutionsToCheck) {
      val bestSolution = inputStates.dequeue()
      val pathIndex = bestSolution.path.length

      hypotheses.lift(pathIndex) match {
        case None =>
          checkedSolutions += 1
          best match {
            case None => best = Some(bestSolution)
            case Some(previous) if bestSolution.score > previous.score => best = Some(bestSolution)
            case _ =>
          }
        case Some(nodes) =>
          nodes.map { case (next, i) =>
            val current = hypotheses(pathIndex - 1)(bestSolution.path.last)._1
            scoreTransition(current, next)
              .map { transitionScore =>
                inputStates.enqueue(Solution(bestSolution.path :+ i, bestSolution.score + transitionScore))
              }
          }
      }
    }

    best.map { case Solution(path, _) => path.zipWithIndex.map { case (n, m) => hypotheses.apply(m).apply(n)._1 }}
  }

}
