package nl.roelofruis.artamus.analysis.rna

import nl.roelofruis.artamus.degree.Model.{Degree, PitchDescriptor, Quality, Scale}

object Model {

  final case class RNARules(
    numResultsRequired: Int,
    penalties: RNAPenalties,
    functions: List[RNAFunction],
    transitions: List[RNATransition]
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

}
