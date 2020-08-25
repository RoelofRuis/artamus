package nl.roelofruis.artamus.analysis.rna

import nl.roelofruis.artamus.analysis.TuningMaths
import nl.roelofruis.artamus.analysis.rna.Model._
import nl.roelofruis.artamus.degree.Model._

import scala.annotation.tailrec
import scala.collection.mutable

case class RNA(tuning: Tuning, rules: RNARules) extends TuningMaths {

  import nl.roelofruis.artamus.tuning.Printer._ // TODO: remove

  final case class DegreeHypothesis(
    chord: Chord,
    degree: Degree,
    key: Key
  )

  private final case class State(
    chord: Chord,
    degree: Degree,
    key: Key,
    weight: Int
  )

  private final case class Graph(
    nodeList: List[Chord],
    stateList: Seq[State]
  ) extends Ordered[Graph] {
    val score: Int = stateList.foldRight(0) { case (state, sum) => sum + state.weight}
    override def compare(that: Graph): Int = score.compareTo(that.score)
  }

  def nameDegrees(chords: Seq[Chord]): Seq[Degree] = {

    val successes= graphSearch(chords, 1)

    printGraphs(successes.dequeueAll)

    def printGraphs(graphs: Seq[Graph]): Unit = {
      graphs.foreach { graph =>
        println("> ")
        graph.stateList.map {
          case State(chord, degree, key, weight) =>
            val textChord = tuning.printChord(chord)
            val textDegree = tuning.printDegree(degree)
            val textKey = tuning.printKey(key)
            s"$textChord: $textDegree in $textKey [$weight]"
        }.foreach(println)
      }
    }

    Seq()
  }

  private def graphSearch(
    chords: Seq[Chord],
    resultsRequired: Int
  ): mutable.PriorityQueue[Graph] = {
    val inputStates = new mutable.PriorityQueue[Graph]()
    inputStates.enqueue(Graph(chords.toList, Seq()))
    val successes = new mutable.PriorityQueue[Graph]()

    @tailrec
    def search: mutable.PriorityQueue[Graph] = {
      if (successes.size >= resultsRequired) {
        println(s"Search terminated with [${successes.size}] results")
        successes
      } else if (inputStates.isEmpty) {
        println("Search exhausted")
        successes
      } else {
        val graph = inputStates.dequeue()

        graph.nodeList match {
          case Nil => successes.enqueue(graph)
          case chord :: tail =>
            val hypotheses = findPossibleDegrees(chord)

            graph.stateList.lastOption match {
              case None => hypotheses.foreach { hypothesis =>
                  inputStates.enqueue(
                    Graph(
                      tail,
                      Seq(State(hypothesis.chord, hypothesis.degree, hypothesis.key, 0))
                    ))
                }
              case Some(currentState) =>
                hypotheses
                  .flatMap(hyp => findTransitions(currentState, hyp))
                  .foreach { state => inputStates.enqueue(Graph(tail, graph.stateList :+ state)) }
            }
        }
        search
      }
    }

    search
  }

  private def findTransitions(currentState: State, possibleNext: DegreeHypothesis): Seq[State] = {
    if (currentState.key != possibleNext.key) {
      Seq(State(possibleNext.chord, possibleNext.degree, possibleNext.key, rules.keyChangePenalty))
    } else {
      rules
        .transitions
        .flatMap { transition =>
          val isWeighted = transition.from == currentState.degree && transition.to == possibleNext.degree
          if (! isWeighted) None
          else Some(State(possibleNext.chord, possibleNext.degree, possibleNext.key, transition.weight))
        }
    }

  }

  def findPossibleDegrees(chord: Chord): List[DegreeHypothesis] = {
    rules
      .functions
      .filter(_.quality == chord.quality)
      .flatMap { function =>
        function.options.map { option =>
          val degreeRoot =  chord.root - option.keyInterval
          DegreeHypothesis(
            chord,
            option.explainedAs,
            Key(degreeRoot, option.scale)
          )
        }
      }
  }

}
