package nl.roelofruis.artamus.analysis.rna

import nl.roelofruis.artamus.analysis.TuningMaths
import nl.roelofruis.artamus.analysis.rna.Analyser.{RNANode, RNANodeHypothesis}
import nl.roelofruis.artamus.analysis.rna.Model._
import nl.roelofruis.artamus.degree.Model._
import nl.roelofruis.artamus.search.GraphSearch
import nl.roelofruis.artamus.search.GraphSearch.{Graph, Node}

object Analyser {
  final case class RNANodeHypothesis(
    chord: Chord,
    degree: Degree,
    key: Key
  )

  final case class RNANode(
    chord: Chord,
    degree: Degree,
    key: Key,
    weight: Int
  ) extends Node
}

case class Analyser(tuning: Tuning, rules: RNARules) extends TuningMaths {

  def nameDegrees(chords: Seq[Chord]): Seq[Graph[RNANode]] = {
    GraphSearch.bestFirst(
      rules.numResultsRequired,
      chords,
      findPossibleNodes,
      findTransitions
    )
  }

  private def findPossibleNodes: Chord => List[RNANodeHypothesis] = chord => {
    rules
      .functions
      .filter(_.quality == chord.quality)
      .flatMap { function =>
        function.options.flatMap { option =>
          val degreeRoot = chord.root - option.keyInterval

          val hypothesis = RNANodeHypothesis(
            chord,
            option.explainedAs,
            Key(degreeRoot, option.scale)
          )

          if (function.allowEnharmonicEquivalents) {
            degreeRoot.enharmonicEquivalent match {
              case None => Seq(hypothesis)
              case Some(equivalentRoot) => Seq(hypothesis, hypothesis.copy(key=Key(equivalentRoot, option.scale)))
            }
          } else Seq(hypothesis)
        }
      }
  }

  private def findTransitions: (Option[RNANode], RNANodeHypothesis) => Seq[RNANode] = (current, hypothesis) => {
    current match {
      case None => Seq(RNANode(hypothesis.chord, hypothesis.degree, hypothesis.key, 0))
      case Some(currentNode) =>
        val keyChangePenalty = if (currentNode.key != hypothesis.key) rules.penalties.keyChange else 0
        val newStates = rules
          .transitions
          .filter(transition => transition.from == currentNode.degree && transition.to == hypothesis.degree)
          .map { transition =>
            RNANode(hypothesis.chord, hypothesis.degree, hypothesis.key, transition.weight + keyChangePenalty)
          }
        if (newStates.isEmpty) {
          Seq(RNANode(
            hypothesis.chord,
            hypothesis.degree,
            hypothesis.key,
            rules.penalties.unknownTransition + keyChangePenalty
          ))
        }
        else newStates
    }
  }

}
