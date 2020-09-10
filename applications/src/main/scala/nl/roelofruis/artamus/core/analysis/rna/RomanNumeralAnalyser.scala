package nl.roelofruis.artamus.core.analysis.rna

import nl.roelofruis.artamus.application.Model.Settings
import nl.roelofruis.artamus.core.Containers.Windowed
import nl.roelofruis.artamus.core.Pitched._
import nl.roelofruis.artamus.core.algorithms.GraphSearch
import nl.roelofruis.artamus.core.analysis.TunedMaths
import nl.roelofruis.artamus.core.analysis.rna.Model._

case class RomanNumeralAnalyser(tuning: Settings, rules: RNARules) extends TunedMaths {

  type WindowedRNANode = Windowed[RNANode]

  def nameDegrees(chordTrack: ChordTrack): AnalysedTrack = {
    val analysis = GraphSearch.bestFirst(
      rules.maxSolutionsToCheck,
      findPossibleNodes,
      scoreTransition
    )(chordTrack)

    analysis match {
      case Some(result) if result.length >= 1 =>
        val firstRoot = result.head.element.key.root
        result.map { node =>
          Windowed(
            node.window,
            RNAAnalysedChord(
              node.element.chord,
              node.element.key,
              node.element.degree,
              node.element.key.copy(root = node.element.key.root - firstRoot)
            )
          )
        }
      case _ => Seq()
    }
  }

  def findPossibleNodes: Windowed[Chord] => List[WindowedRNANode] = chord => {
    rules
      .interpretations
      .filter(_.quality == chord.element.quality)
      .flatMap { function =>
        function.options.flatMap { option =>
          val degreeRoot = chord.element.root - option.keyInterval

          val hypothesis = RNANode(
            chord.element,
            option.explainedAs,
            Key(degreeRoot, option.scale)
          )

          if (function.allowEnharmonicEquivalents) {
            degreeRoot.enharmonicEquivalent match {
              case None => Seq(Windowed(chord.window, hypothesis))
              case Some(equivalentRoot) => Seq(
                Windowed(chord.window, hypothesis),
                Windowed(chord.window, hypothesis.copy(key=Key(equivalentRoot, option.scale)))
              )
            }
          } else Seq(Windowed(chord.window, hypothesis))
        }
      }
  }

  def scoreTransition: (WindowedRNANode, WindowedRNANode) => Option[Int] = (currentWindow, nextWindow) => {
    val current = currentWindow.element
    val next = nextWindow.element
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
