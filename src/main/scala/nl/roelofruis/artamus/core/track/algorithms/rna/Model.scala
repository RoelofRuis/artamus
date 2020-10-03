package nl.roelofruis.artamus.core.track.algorithms.rna

import nl.roelofruis.artamus.core.track.Pitched._

object Model {

  final case class RNARules(
    maxSolutionsToCheck: Int,
    unknownTransitionPenalty: Int,
    unknownKeyChangePenalty: Int,
    keyChanges: List[RNAKeyChange],
    interpretations: List[RNAInterpretation],
    transitions: List[RNATransition],
    qualityReductions: Seq[QualityReduction]
  )

  final case class QualityReduction(
    intervals: Seq[IntervalDescription],
    possibleQualities: Seq[DegreeQuality]
  )

  final case class IntervalDescription(
    shouldContain: Boolean,
    interval: MatchableInterval
  )

  trait MatchableInterval
  final case class ExactInterval(interval: PitchDescriptor) extends MatchableInterval
  final case class AnyIntervalOnStep(step: Int) extends MatchableInterval

  type DegreeQuality = String

  final case class RNAKeyChange(
    scaleFrom: Scale,
    keyTo: Key,
    weight: Int
  )

  final case class RNAPenalties(
    keyChange: Int,
    unknownTransition: Int
  )

  final case class RNATransition(
    from: Degree,
    to: Degree,
    weight: Int
  )

  final case class RNAInterpretation(
    qualityTag: DegreeQuality,
    options: List[RNAInterpretationOption],
    allowEnharmonicEquivalents: Boolean
  )

  final case class RNAInterpretationOption(
    keyInterval: PitchDescriptor,
    scale: Scale,
    explainedAs: Degree
  )

  final case class RNANode(
    chord: Chord,
    degree: Degree,
    key: Key
  )

  final case class RNAAnalysedChord(
    chord: Chord,
    relativeKey: Key,
    degree: Degree,
    absoluteKey: Key,
  )

}
