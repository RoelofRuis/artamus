package nl.roelofruis.artamus.core.analysis.rna

import nl.roelofruis.artamus.core.Containers.Windowed
import nl.roelofruis.artamus.core.Pitched._

object Model {

  final case class RNARules(
    maxSolutionsToCheck: Int,
    unknownTransitionPenalty: Int,
    unknownKeyChangePenalty: Int,
    keyChanges: List[RNAKeyChange],
    interpretations: List[RNAInterpretation],
    transitions: List[RNATransition]
  )

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
    quality: Quality,
    options: List[RNAInterpretationOption],
    allowEnharmonicEquivalents: Boolean
  )

  final case class RNAInterpretationOption(
    keyInterval: PitchDescriptor,
    scale: Scale,
    explainedAs: Degree
  )

  final case class RNANode(
    chord: Windowed[Chord],
    degree: Degree,
    key: Key
  )

  final case class RNAAnalysedChord(
    chord: Windowed[Chord],
    relativeKey: Key,
    degree: Degree,
    absoluteKey: Key,
  )

}
