package nl.roelofruis.artamus.analysis.rna

import nl.roelofruis.artamus.analysis.rna.Model.{AllowedDegree, AllowedKeyInterval, AllowedScale, AnyDegree, AnyKeyInterval, AnyScale, RNARules, SpecificDegree, SpecificKeyInterval, SpecificScale, Transition, TransitionDescription, TransitionEnd, TransitionRule, TransitionStart}
import nl.roelofruis.artamus.degree.Model.Tuning
import nl.roelofruis.artamus.util.File
import spray.json._

object RNALoader {
  import nl.roelofruis.artamus.analysis.rna.RNALoader.FileModel.TextRNARules

  def loadRNA(tuning: Tuning): RNARules = {
    def parseRule(rule: String): TransitionRule = {
      if (rule == "S") TransitionStart
      else if (rule == "E") TransitionEnd
      else {
        val parts = rule.split(':')
        TransitionDescription(
          parseDegree(parts(0)),
          parseKeyInterval(parts(1)),
          parseScale(parts(2))
        )
      }
    }

    def parseDegree(degree: String): AllowedDegree = {
      if (degree == "_") AnyDegree
      else SpecificDegree(tuning.parseDegreeDescriptor.run(degree).value)
    }

    def parseKeyInterval(interval: String): AllowedKeyInterval = {
      if (interval == "_") AnyKeyInterval
      else SpecificKeyInterval(tuning.parseInterval.run(interval).value)
    }

    def parseScale(scale: String): AllowedScale = {
      if (scale == "_") AnyScale
      else SpecificScale(tuning.parseScale.run(scale).value)
    }

    val textRules = File.load[TextRNARules]("applications/res/rna_rules.json").get // TODO: remove get

    val transitions = textRules.transitions.map { textTransition =>
      println(textTransition.description)
      Transition(
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
      description: String,
      premise: String,
      transition: String,
      weight: Int
    )

    object TextRNATransition {
      implicit val textRNATransitionFormat: JsonFormat[TextRNATransition] = jsonFormat5(TextRNATransition.apply)
    }
  }

}
