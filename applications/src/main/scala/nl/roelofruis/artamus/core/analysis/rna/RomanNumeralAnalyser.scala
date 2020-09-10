package nl.roelofruis.artamus.core.analysis.rna

import nl.roelofruis.artamus.application.Model.Settings
import nl.roelofruis.artamus.core.Containers.Windowed
import nl.roelofruis.artamus.core.Pitched._
import nl.roelofruis.artamus.core.algorithms.GraphSearch
import nl.roelofruis.artamus.core.analysis.TunedMaths
import nl.roelofruis.artamus.core.analysis.rna.Model._

case class RomanNumeralAnalyser(tuning: Settings, rules: RNARules) extends TunedMaths {

  def nameDegrees(chords: Seq[Windowed[Chord]]): Option[Array[RNAAnalysedChord]] = {
    val analysis = GraphSearch.bestFirst(
      rules.maxSolutionsToCheck,
      findPossibleNodes,
      scoreTransition
    )(chords)

    analysis match {
      case Some(result) if result.length >= 1 =>
        val firstRoot = result.head.key.root
        val analysedChords = result.map { node =>
          RNAAnalysedChord(
            node.chord,
            node.key,
            node.degree,
            node.key.copy(root = node.key.root - firstRoot)
          )
        }
        Some(analysedChords)
      case _ => None
    }
  }

  def findPossibleNodes: Windowed[Chord] => List[RNANode] = chord => {
    rules
      .interpretations
      .filter(_.quality == chord.element.quality)
      .flatMap { function =>
        function.options.flatMap { option =>
          val degreeRoot = chord.element.root - option.keyInterval

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
