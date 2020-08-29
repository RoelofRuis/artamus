package nl.roelofruis.artamus.core.analysis.rna

import nl.roelofruis.artamus.core.Pitched.{Chord, Degree, Key, PitchDescriptor, Quality, Scale}
import nl.roelofruis.artamus.core.algorithms.GraphSearch.Node

object Model {

  final case class RNARules(
    numResultsRequired: Int,
    unknownTransitionPenalty: Int,
    unknownKeyChangePenalty: Int,
    keyChanges: List[RNAKeyChange],
    functions: List[RNAFunction],
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

  final case class RNAFunction(
    quality: Quality,
    options: List[RNAFunctionOption],
    allowEnharmonicEquivalents: Boolean
  )

  final case class RNAFunctionOption(
    keyInterval: PitchDescriptor,
    scale: Scale,
    explainedAs: Degree
  )

  final case class RNANodeHypothesis(
    chord: Chord,
    degree: Degree,
    key: Key
  )

  final case class RNANode(
    chord: Chord,
    degree: Degree,
    key: Key,
    weight: Int
  ) extends Node

}
