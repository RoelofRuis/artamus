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
    degreeQualities: List[DegreeQuality]
  )

  final case class DegreeQuality(
    intervals: Seq[IntervalDescription]
  )

  trait IntervalDescription
  final case class ExactInterval(optional: Boolean, interval: PitchDescriptor) extends IntervalDescription
  final case class AnyIntervalOnStep(optional: Boolean, step: Int) extends IntervalDescription

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
    degreeQuality: DegreeQuality,
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
