package nl.roelofruis.artamus.core.analysis.rna

import nl.roelofruis.artamus.application.Model.Settings
import nl.roelofruis.artamus.core.Pitched._
import nl.roelofruis.artamus.core.algorithms.GraphSearch
import nl.roelofruis.artamus.core.analysis.TunedMaths
import nl.roelofruis.artamus.core.analysis.rna.Model._

case class RomanNumeralAnalyser(tuning: Settings, rules: RNARules) extends TunedMaths {

  def nameDegrees(chords: Seq[Chord]): Option[Array[RNANode]] = {
    GraphSearch.bestFirst(
      rules.maxSolutionsToCheck,
      findPossibleNodes,
      scoreTransition
    )(chords)
  }

  def findPossibleNodes: Chord => List[RNANode] = chord => {
    rules
      .interpretations
      .filter(_.quality == chord.quality)
      .flatMap { function =>
        function.options.flatMap { option =>
          val degreeRoot = chord.root - option.keyInterval

          val hypothesis = RNANode(
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

  def scoreTransition: (RNANode, RNANode) => Option[Int] = (current, next) => {
      if (current.key == next.key) {
        val maxScore = rules
          .transitions
          .filter(transition => transition.from == current.degree && transition.to == next.degree)
          .map(transition => transition.weight)
          .maxOption

        maxScore match {
          case None if current.degree.relativeTo.isEmpty => Some(rules.unknownTransitionPenalty)
          case s => s
        }
      } else {
        val maxScore = rules
          .keyChanges
          .filter { keyChange =>
            val targetRoot = next.key.root - current.key.root
            val targetRootsMatch = keyChange.keyTo.root.enharmonicEquivalent.contains(targetRoot) || keyChange.keyTo.root == targetRoot
            keyChange.scaleFrom == current.key.scale && targetRootsMatch && keyChange.keyTo.scale == next.key.scale
          }
          .map(keyChange => keyChange.weight)
          .maxOption

        maxScore match {
          case None => Some(rules.unknownKeyChangePenalty)
          case s => s
        }
    }
  }
}
