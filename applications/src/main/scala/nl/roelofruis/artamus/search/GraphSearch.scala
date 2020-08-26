package nl.roelofruis.artamus.search

import scala.annotation.tailrec
import scala.collection.mutable

object GraphSearch {

  trait Node {
    val weight: Int
  }

  final case class Graph[N <: Node](stateList: Seq[N]) extends Ordered[Graph[N]] {
    val score: Int = stateList.foldRight(0) { case (state, sum) => sum + state.weight}
    override def compare(that: Graph[N]): Int = score.compareTo(that.score)
  }

  def bestFirst[S, N <: Node, H](
    numResultsRequired: Int,
    stateSequence: Seq[S],
    findHypotheses: S => Seq[H],
    findTransitions: (Option[N], H) => Seq[N]
  ): Seq[Graph[N]] = {
    val successes = new mutable.PriorityQueue[Graph[N]]()
    val inputStates = new mutable.PriorityQueue[Graph[N]]()
    inputStates.enqueue(Graph[N](Seq()))

    @tailrec
    def search: mutable.PriorityQueue[Graph[N]] = {
      if (inputStates.isEmpty || successes.size >= numResultsRequired) successes
      else {
        val graph = inputStates.dequeue()
        val graphStateSize = graph.stateList.size

        stateSequence.lift(graphStateSize) match {
          case None => successes.enqueue(graph)
          case Some(nextState) =>
            val hypotheses = findHypotheses(nextState)

            hypotheses
                .flatMap(hyp => findTransitions(graph.stateList.lastOption, hyp))
                .foreach { state => inputStates.enqueue(Graph(graph.stateList :+ state)) }
        }
        search
      }
    }

    search.take(numResultsRequired).toSeq
  }

}
