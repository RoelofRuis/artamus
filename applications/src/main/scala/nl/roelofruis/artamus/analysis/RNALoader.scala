package nl.roelofruis.artamus.analysis

import nl.roelofruis.artamus.analysis.RNALoader.FileModel.TextRNARules
import nl.roelofruis.artamus.degree.Model.{PitchDescriptor, Tuning}
import nl.roelofruis.artamus.util.File
import spray.json._

object RNALoader {

  def loadRNA(tuning: Tuning): TextRNARules = {
    val textRules = File.load[TextRNARules]("applications/res/rna_rules.json").get // TODO: remove get

    textRules
  }

  object FileModel extends DefaultJsonProtocol {
    final case class TextRNARules(
      transitions: List[TextRNATransition]
    )

    object TextRNARules {
      implicit val rnaRulesFormat: JsonFormat[TextRNARules] = jsonFormat1(TextRNARules.apply)
    }

    final case class TextRNATransition(
      name: String,
      description: String,
      rule: String,
      weight: Int
    )

    object TextRNATransition {
      implicit val textRNATransitionFormat: JsonFormat[TextRNATransition] = jsonFormat4(TextRNATransition.apply)
    }
  }

}


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
