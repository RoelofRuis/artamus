package nl.roelofruis.artamus.analysis.rna

import nl.roelofruis.artamus.degree.Model.{PitchDescriptor, Scale}

object Model {

  final case class RNARules(
    transitions: List[TransitionType]
  )

  sealed trait AllowedDegree
  final case object AnyDegree extends AllowedDegree
  final case class SpecificDegree(descriptor: PitchDescriptor) extends AllowedDegree

  sealed trait AllowedKeyInterval
  final case object AnyKeyInterval extends AllowedKeyInterval
  final case object SameKeyInterval extends AllowedKeyInterval
  final case class SpecificKeyInterval(interval: PitchDescriptor) extends AllowedKeyInterval

  sealed trait AllowedScale
  final case object AnyScale extends AllowedScale
  final case object SameScale extends AllowedScale
  final case class SpecificScale(scale: Scale) extends AllowedScale

  final case class TransitionDescription(
    degree: AllowedDegree,
    keyInterval: AllowedKeyInterval,
    scale: AllowedScale
  )

  sealed trait TransitionType
  final case class TransitionStart(
    nextState: TransitionDescription,
    weight: Int
  ) extends TransitionType

  final case class Transition(
    currentState: TransitionDescription,
    nextState: TransitionDescription,
    weight: Int
  ) extends TransitionType

  final case class TransitionEnd(
    currentState: TransitionDescription,
    weight: Int
  ) extends TransitionType

}
