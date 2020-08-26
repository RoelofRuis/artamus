package nl.roelofruis.artamus.analysis.rna

import nl.roelofruis.artamus.analysis.TuningMaths
import nl.roelofruis.artamus.analysis.rna.Model._
import nl.roelofruis.artamus.degree.Model._
import nl.roelofruis.artamus.search.GraphSearch
import nl.roelofruis.artamus.search.GraphSearch.{Graph, Node}

case class RNA(tuning: Tuning, rules: RNARules) extends TuningMaths {

  import nl.roelofruis.artamus.tuning.Printer._ // TODO: remove

  final case class RNANodeHypothesis(
    chord: Chord,
    degree: Degree,
    key: Key
  )

  private final case class RNANode(
    chord: Chord,
    degree: Degree,
    key: Key,
    weight: Int
  ) extends Node

  def nameDegrees(chords: Seq[Chord]): Seq[Degree] = {

    val successes = GraphSearch.bestFirst(
      rules.numResultsRequired,
      chords,
      findPossibleNodes,
      findTransitions
    )

    printGraphs(successes)

    def printGraphs(graphs: Seq[Graph[RNANode]]): Unit = {
      graphs.foreach { graph =>
        println(s" > Total score [${graph.score}] >")
        graph.stateList.map {
          case RNANode(chord, degree, key, weight) =>
            val textChord = tuning.printChord(chord)
            val textDegree = tuning.printDegree(degree)
            val textKey = tuning.printKey(key)
            s"$textChord: $textDegree in $textKey [$weight]"
        }.foreach(println)
      }
    }

    Seq()
  }

  def findPossibleNodes: Chord => List[RNANodeHypothesis] = chord => {
    rules
      .functions
      .filter(_.quality == chord.quality)
      .flatMap { function =>
        function.options.map { option =>
          val degreeRoot =  chord.root - option.keyInterval
          RNANodeHypothesis(
            chord,
            option.explainedAs,
            Key(degreeRoot, option.scale)
          )
        }
      }
  }

  private def findTransitions: (Option[RNANode], RNANodeHypothesis) => Seq[RNANode] = (current, possibleNext) => {
    current match {
      case None => Seq(RNANode(possibleNext.chord, possibleNext.degree, possibleNext.key, 0))
      case Some(currentNode) =>
        val keyChangePenalty = if (currentNode.key != possibleNext.key) rules.penalties.keyChange else 0
        val newStates = rules
          .transitions
          .filter(transition => transition.from == currentNode.degree && transition.to == possibleNext.degree)
          .map { transition =>
            RNANode(possibleNext.chord, possibleNext.degree, possibleNext.key, transition.weight + keyChangePenalty)
          }
        if (newStates.isEmpty) {
          Seq(RNANode(
            possibleNext.chord,
            possibleNext.degree,
            possibleNext.key,
            rules.penalties.unknownTransition + keyChangePenalty
          ))
        }
        else newStates
    }
  }

}
