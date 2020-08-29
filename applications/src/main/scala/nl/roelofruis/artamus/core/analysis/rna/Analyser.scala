package nl.roelofruis.artamus.core.analysis.rna

import nl.roelofruis.artamus.core.Pitched._
import nl.roelofruis.artamus.core.analysis.TuningMaths
import nl.roelofruis.artamus.core.analysis.rna.Model._
import nl.roelofruis.artamus.core.math.algorithms.GraphSearch
import nl.roelofruis.artamus.core.math.algorithms.GraphSearch.Graph
import nl.roelofruis.artamus.settings.Model.Settings

case class Analyser(tuning: Settings, rules: RNARules) extends TuningMaths {

  def nameDegrees(chords: Seq[Chord]): Option[Graph[RNANode]] = {
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
        if (newStates.isEmpty && currentNode.degree.relativeTo.isEmpty) Seq(hypothesisToNode(hypothesis, rules.penalties.unknownTransition))
        else newStates
      case Some(_) => Seq(hypothesisToNode(hypothesis, rules.penalties.keyChange))
    }
  }

  private def hypothesisToNode(hyp: RNANodeHypothesis, weight: Int): RNANode = RNANode(hyp.chord, hyp.degree, hyp.key, weight)

}
