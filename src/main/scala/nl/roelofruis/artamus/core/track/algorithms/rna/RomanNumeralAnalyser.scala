package nl.roelofruis.artamus.core.track.algorithms.rna

import nl.roelofruis.artamus.core.common.Temporal.Windowed
import nl.roelofruis.artamus.core.common.algorithms.GraphSearch
import nl.roelofruis.artamus.core.track.Layer.{ChordSeq, RomanNumeralSeq}
import nl.roelofruis.artamus.core.track.Pitched._
import nl.roelofruis.artamus.core.track.algorithms.TunedMaths
import nl.roelofruis.artamus.core.track.algorithms.TunedMaths.TuningDefinition
import nl.roelofruis.artamus.core.track.algorithms.rna.Model._

case class RomanNumeralAnalyser(settings: TuningDefinition, rules: RNARules) extends TunedMaths {

  private type WindowedRNANode = Windowed[RNANode]

  def analyse(chordTrack: ChordSeq): RomanNumeralSeq = {
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
    val degreeQualities = matchingQualities(chord.element.quality)
    rules
      .interpretations
      .filter(i => degreeQualities.contains(i.degreeQuality))
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

  private def matchingQualities(quality: Quality): Seq[DegreeQuality] = {
    val matches = rules.degreeQualities.map { degreeQuality =>
      val score = degreeQuality.intervals.foldLeft(0) { case (acc, descr) =>
        val (optional, contains) = descr match {
          case AnyIntervalOnStep(optional, step) => (optional, quality.intervals.map(_.step).contains(step))
          case ExactInterval(optional, interval) => (optional, quality.intervals.contains(interval))
        }
        if (contains) acc + 1
        else if (optional) acc
        else -1
      }
      (score, degreeQuality)
    }
      .filter { case (score, _) => score >= 0 }

    val maxScore = matches.maxBy { case (score, _) => score }._1
    matches.filter { case (score, _) => score == maxScore }.map { case (_, degreeQuality) => degreeQuality }
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
