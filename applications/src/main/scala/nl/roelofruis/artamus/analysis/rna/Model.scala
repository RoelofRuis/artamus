package nl.roelofruis.artamus.analysis.rna

import nl.roelofruis.artamus.degree.Model.{PitchDescriptor, Scale}

object Model {

  final case class RNARules(
    transitions: List[Transition]
  )

  sealed trait AllowedDegree
  final case object AnyDegree extends AllowedDegree
  final case class SpecificDegree(descriptor: PitchDescriptor) extends AllowedDegree

  sealed trait AllowedKeyInterval
  final case object AnyKeyInterval extends AllowedKeyInterval
  final case class SpecificKeyInterval(interval: PitchDescriptor) extends AllowedKeyInterval

  sealed trait AllowedScale
  final case object AnyScale extends AllowedScale
  final case class SpecificScale(scale: Scale) extends AllowedScale

  sealed trait TransitionRule
  final case object TransitionStart extends TransitionRule
  final case object TransitionEnd extends TransitionRule
  final case class TransitionDescription(
    degree: AllowedDegree,
    keyInterval: AllowedKeyInterval,
    scale: AllowedScale
  ) extends TransitionRule

  final case class Transition(
    previousState: TransitionRule,
    currentState: TransitionRule,
    weight: Int
  )

}
