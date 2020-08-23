package nl.roelofruis.artamus.analysis.rna

import nl.roelofruis.artamus.degree.Model.Tuning
import nl.roelofruis.artamus.analysis.rna.RNALoader.FileModel.TextRNARules
import nl.roelofruis.artamus.util.File
import spray.json._

object RNALoader {

  import nl.roelofruis.artamus.tuning.Parser._

  def loadRNA(tuning: Tuning): TextRNARules = {
    val textRules = File.load[TextRNARules]("applications/res/rna_rules.json").get // TODO: remove get

    textRules.transitions.map { textTransition =>

    }

    textRules

    def parseRule(rule: String) = {
      if (rule == "S") ??? // START NODE
      else {
        val parts = rule.split(':')
        val degree = parseDegree(parts(0))
        val step = parseStep(parts(1))
        val scale = parseScale(parts(2))
      }
    }

    def parseDegree(degree: String) = {
      if (degree == "_") ??? // ANY DEGREE
      else tuning.parseDegreeDescriptor.run(degree).value
    }

    def parseStep(step: String) = {
      if (step == "_") ??? // ANY STEP
      else tuning.parseInterval.run(step)
    }

    def parseScale(scale: String) = {
      if (scale == "_") ??? // ANY SCALE
      else if (scale == "s") ??? // SAME SCALE
      else tuning.parseScale.run(scale).value
    }

    ???
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
      premise: String,
      transition: String,
      relative: Boolean,
      weight: Int
    )

    object TextRNATransition {
      implicit val textRNATransitionFormat: JsonFormat[TextRNATransition] = jsonFormat6(TextRNATransition.apply)
    }
  }

}
