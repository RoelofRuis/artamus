package nl.roelofruis.artamus.core.track.algorithms.functional

import nl.roelofruis.artamus.core.track.Pitched.PitchDescriptor

object Model {

  final case class FunctionalAnalysisRules(
    tagReductions: Seq[TagReduction]
  )

  final case class TagReduction(
    intervals: Seq[IntervalDescription],
    possibleTags: Seq[QualityTag]
  )

  final case class IntervalDescription(
    shouldContain: Boolean,
    interval: MatchableInterval
  )

  trait MatchableInterval
  final case class ExactInterval(interval: PitchDescriptor) extends MatchableInterval
  final case class AnyIntervalOnStep(step: Int) extends MatchableInterval

  final case class QualityTag(
    name: String
  )

}
