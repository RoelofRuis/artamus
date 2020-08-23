package nl.roelofruis.artamus.analysis.rna

import nl.roelofruis.artamus.degree.Model.PitchDescriptor

object Model {

  sealed trait DegreePitch
  final case object AnyPitch extends DegreePitch
  final case class SpecificPitch(descriptor: PitchDescriptor) extends DegreePitch

  sealed trait KeyInterval
  final case object AnyInterval extends KeyInterval
  final case object SameInterval extends KeyInterval
  final case class SpecificInterval(interval: PitchDescriptor) extends KeyInterval

  sealed trait TransitionState
  final case object TransitionStart extends TransitionState
  final case object TransitionEnd extends TransitionState
  final case class Transition(
    degreePitch: DegreePitch,
    keyInterval: KeyInterval,
  ) extends TransitionState

  final case class TransitionRule(
    previousState: TransitionState,
    currentState: TransitionState,
    score: Long
  )

}
