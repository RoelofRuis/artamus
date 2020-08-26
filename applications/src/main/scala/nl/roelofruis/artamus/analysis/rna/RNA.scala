package nl.roelofruis.artamus.analysis.rna

import nl.roelofruis.artamus.analysis.TuningMaths
import nl.roelofruis.artamus.analysis.rna.Model._
import nl.roelofruis.artamus.degree.Model._

import scala.annotation.tailrec
import scala.collection.mutable

case class RNA(tuning: Tuning, rules: RNARules) extends TuningMaths {

  import nl.roelofruis.artamus.tuning.Printer._ // TODO: remove

  final case class NodeHypothesis(
    chord: Chord,
    degree: Degree,
    key: Key
  )

  private final case class Node(
    chord: Chord,
    degree: Degree,
    key: Key,
    weight: Int
  )

  private final case class Graph(stateList: Seq[Node]) extends Ordered[Graph] {
    val score: Int = stateList.foldRight(0) { case (state, sum) => sum + state.weight}
    override def compare(that: Graph): Int = score.compareTo(that.score)
  }

  def nameDegrees(chords: Seq[Chord]): Seq[Degree] = {

    val successes = graphSearch(chords)

    printGraphs(successes.dequeueAll)

    def printGraphs(graphs: Seq[Graph]): Unit = {
      graphs.foreach { graph =>
        println(s" > Total score [${graph.score}] >")
        graph.stateList.map {
          case Node(chord, degree, key, weight) =>
            val textChord = tuning.printChord(chord)
            val textDegree = tuning.printDegree(degree)
            val textKey = tuning.printKey(key)
            s"$textChord: $textDegree in $textKey [$weight]"
        }.foreach(println)
      }
    }

    Seq()
  }

  private def graphSearch(chords: Seq[Chord]): mutable.PriorityQueue[Graph] = {
    val inputStates = new mutable.PriorityQueue[Graph]()
    inputStates.enqueue(Graph(Seq()))
    val successes = new mutable.PriorityQueue[Graph]()

    @tailrec
    def search: mutable.PriorityQueue[Graph] = {
      if (successes.size >= rules.numResultsRequired) {
        println(s"Search terminated with [${successes.size}] results")
        successes
      } else if (inputStates.isEmpty) {
        println("Search exhausted")
        successes
      } else {
        val graph = inputStates.dequeue()
        val graphStateSize = graph.stateList.size

        chords.lift(graphStateSize) match {
          case None => successes.enqueue(graph)
          case Some(nextChord) =>
            val hypotheses = findPossibleNodes(nextChord)

            if (hypotheses.isEmpty) println(s"WARNING: EMPTY HYPOTHESIS @ $nextChord")

            if (graphStateSize == 0) {
              hypotheses.foreach { hypothesis =>
                inputStates.enqueue(Graph(Seq(Node(hypothesis.chord, hypothesis.degree, hypothesis.key, 0))))
              }
            } else {
              hypotheses
                .flatMap(hyp => findTransitions(graph.stateList.last, hyp))
                .foreach { state => inputStates.enqueue(Graph(graph.stateList :+ state)) }
            }
        }
        search
      }
    }

    search
  }

  private def findTransitions(currentNode: Node, possibleNext: NodeHypothesis): Seq[Node] = {
    val keyChangePenalty = if (currentNode.key != possibleNext.key) rules.penalties.keyChange else 0
    val newStates = rules
      .transitions
      .filter(transition => transition.from == currentNode.degree && transition.to == possibleNext.degree)
      .map { transition =>
        Node(possibleNext.chord, possibleNext.degree, possibleNext.key, transition.weight + keyChangePenalty)
      }
    if (newStates.isEmpty) {
      Seq(Node(
        possibleNext.chord,
        possibleNext.degree,
        possibleNext.key,
        rules.penalties.unknownTransition + keyChangePenalty
      ))
    }
    else newStates
  }

  def findPossibleNodes(chord: Chord): List[NodeHypothesis] = {
    rules
      .functions
      .filter(_.quality == chord.quality)
      .flatMap { function =>
        function.options.map { option =>
          val degreeRoot =  chord.root - option.keyInterval
          NodeHypothesis(
            chord,
            option.explainedAs,
            Key(degreeRoot, option.scale)
          )
        }
      }
  }

}
