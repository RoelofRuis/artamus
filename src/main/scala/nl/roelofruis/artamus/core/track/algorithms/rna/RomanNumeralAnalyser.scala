package nl.roelofruis.artamus.core.track.algorithms.rna

import nl.roelofruis.artamus.application.Model.Settings
import nl.roelofruis.artamus.core.common.Temporal.Windowed
import nl.roelofruis.artamus.core.track.Pitched._
import nl.roelofruis.artamus.core.common.algorithms.GraphSearch
import nl.roelofruis.artamus.core.track.Layer.{ChordSeq, RomanNumeralSeq}
import nl.roelofruis.artamus.core.track.algorithms.TunedMaths
import nl.roelofruis.artamus.core.track.algorithms.rna.Model._

case class RomanNumeralAnalyser(settings: Settings, rules: RNARules) extends TunedMaths {

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
    val qualityTags = matchingTags(chord.element.quality)
    rules
      .interpretations
      .filter(i => qualityTags.contains(i.qualityTag))
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

  private def matchingTags(quality: Quality): Seq[DegreeQuality] = {
    rules.qualityReductions.map { reduction =>
      val score = reduction.intervals.foldLeft(0) { case (acc, descr) =>
        if (acc == -1) acc
        else {
          val contains = {
            descr.interval match {
              case AnyIntervalOnStep(step) => quality.intervals.map(_.step).contains(step)
              case ExactInterval(interval) => quality.intervals.contains(interval)
            }
          }
          if (! descr.shouldContain && contains) -1
          else {
            if (contains) acc + 1 else acc
          }
        }
      }
      (score, reduction.possibleQualities)
    }
      .filter { case (score, _) => score >= 0 }
      .maxByOption { case (score, _) => score }
      .map(_._2).getOrElse(Seq())
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
