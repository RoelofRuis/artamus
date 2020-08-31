package nl.roelofruis.artamus.core.analysis.rna

import nl.roelofruis.artamus.application.Model.Settings
import nl.roelofruis.artamus.core.Pitched._
import nl.roelofruis.artamus.core.algorithms.GraphSearch
import nl.roelofruis.artamus.core.analysis.TunedMaths
import nl.roelofruis.artamus.core.analysis.rna.Model._

case class Analyser(tuning: Settings, rules: RNARules) extends TunedMaths {

  def nameDegrees(chords: Seq[Chord]): Option[Seq[RNANode]] = {
    GraphSearch.bestFirst(
      rules.numResultsRequired,
      chords,
      findPossibleNodes,
      findTransitions
    ).headOption
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
      case Some(currentNode) if currentNode.key == hypothesis.key =>
        val newStates = rules
          .transitions
          .filter(transition => transition.from == currentNode.degree && transition.to == hypothesis.degree)
          .map(transition => hypothesisToNode(hypothesis, transition.weight))
        if (newStates.isEmpty && currentNode.degree.relativeTo.isEmpty) Seq(hypothesisToNode(hypothesis, rules.unknownTransitionPenalty))
        else newStates
      case Some(currentNode) =>
        val keyChanges = rules
          .keyChanges
          .filter { keyChange =>
            val targetRoot = hypothesis.key.root - currentNode.key.root
            val targetRootsMatch = keyChange.keyTo.root.enharmonicEquivalent.contains(targetRoot) || keyChange.keyTo.root == targetRoot
            keyChange.scaleFrom == currentNode.key.scale && targetRootsMatch && keyChange.keyTo.scale == hypothesis.key.scale
          }
          .map(keyChange => hypothesisToNode(hypothesis, keyChange.weight))
        if (keyChanges.isEmpty) Seq(hypothesisToNode(hypothesis, rules.unknownKeyChangePenalty))
        else keyChanges
    }
  }

  private def hypothesisToNode(hyp: RNANodeHypothesis, weight: Int): RNANode = RNANode(hyp.chord, hyp.degree, hyp.key, weight)

}
