package nl.roelofruis.artamus.analysis.rna

import nl.roelofruis.artamus.analysis.rna.Model._
import nl.roelofruis.artamus.degree.Model.Tuning
import nl.roelofruis.artamus.util.File
import spray.json._

object RNALoader {
  import nl.roelofruis.artamus.analysis.rna.RNALoader.FileModel.TextRNARules

  def loadRNA(tuning: Tuning): RNARules = {
    def parseRule(rule: String): TransitionDescription = {
      val parts = rule.split(':')
      TransitionDescription(
        parseDegree(parts(0)),
        parseKeyInterval(parts(1)),
        parseScale(parts(2))
      )
    }

    def parseDegree(degree: String): AllowedDegree = {
      if (degree == "_") AnyDegree
      else SpecificDegree(tuning.parseDegreeDescriptor.run(degree).value)
    }

    def parseKeyInterval(interval: String): AllowedKeyInterval = {
      if (interval == "_") AnyKeyInterval
      else if (interval == "x") SameKeyInterval
      else SpecificKeyInterval(tuning.parseInterval.run(interval).value)
    }

    def parseScale(scale: String): AllowedScale = {
      if (scale == "_") AnyScale
      else if (scale == "x") SameScale
      else SpecificScale(tuning.parseScale.run(scale).value)
    }

    val textRules = File.load[TextRNARules]("applications/res/rna_rules.json").get // TODO: remove get

    val transitions = textRules.transitions.map { textTransition =>
      if (textTransition.premise == "START") TransitionStart(
        parseRule(textTransition.transition),
        textTransition.weight
      )
      else if (textTransition.transition == "END") TransitionEnd(
        parseRule(textTransition.premise),
        textTransition.weight
      )
      else Transition(
        parseRule(textTransition.premise),
        parseRule(textTransition.transition),
        textTransition.weight
      )
    }

    RNARules(transitions)
  }

  private object FileModel extends DefaultJsonProtocol {
    final case class TextRNARules(
      transitions: List[TextRNATransition]
    )

    object TextRNARules {
      implicit val rnaRulesFormat: JsonFormat[TextRNARules] = jsonFormat1(TextRNARules.apply)
    }

    final case class TextRNATransition(
      name: String,
      premise: String,
      transition: String,
      weight: Int
    )

    object TextRNATransition {
      implicit val textRNATransitionFormat: JsonFormat[TextRNATransition] = jsonFormat4(TextRNATransition.apply)
    }
  }

}
