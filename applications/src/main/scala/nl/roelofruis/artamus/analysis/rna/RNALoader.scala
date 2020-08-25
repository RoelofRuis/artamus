package nl.roelofruis.artamus.analysis.rna

import nl.roelofruis.artamus.analysis.rna.Model._
import nl.roelofruis.artamus.degree.Model.Tuning
import nl.roelofruis.artamus.util.File
import spray.json._

object RNALoader {
  import nl.roelofruis.artamus.analysis.rna.RNALoader.FileModel.TextRNARules

  def loadRNA(tuning: Tuning): RNARules = {
    def parseOption(option: String): RNAFunctionOption = {
      val parts = option.split(':')

      RNAFunctionOption(
        tuning.parseInterval.run(parts(0)).value,
        tuning.parseScale.run(parts(1)).value,
        tuning.parseDegree.run(parts(2)).value
      )
    }

    val textRules = File.load[TextRNARules]("applications/res/rna_rules.json").get // TODO: remove get

    val functions = textRules.functions.map { textFunction =>
      RNAFunction(
        tuning.parseQuality.run(textFunction.quality).value,
        textFunction.options.map(parseOption)
      )
    }

    val transitions = textRules.transitions.map { textTransition =>
      RNATransition(
        tuning.parseDegree.run(textTransition.from).value,
        tuning.parseDegree.run(textTransition.to).value,
        textTransition.weight
      )
    }

    RNARules(
      textRules.keyChangePenalty,
      functions,
      transitions
    )
  }

  private object FileModel extends DefaultJsonProtocol {
    final case class TextRNARules(
      keyChangePenalty: Int,
      functions: List[TextRNAFunction],
      transitions: List[TextRNATransition]
    )

    object TextRNARules {
      implicit val rnaRulesFormat: JsonFormat[TextRNARules] = jsonFormat3(TextRNARules.apply)
    }

    final case class TextRNAFunction(
      quality: String,
      options: List[String]
    )

    object TextRNAFunction {
      implicit val rnafunctionFormat: JsonFormat[TextRNAFunction] = jsonFormat2(TextRNAFunction.apply)
    }

    final case class TextRNATransition(
      from: String,
      to: String,
      weight: Int
    )

    object TextRNATransition {
      implicit val textRNATransitionFormat: JsonFormat[TextRNATransition] = jsonFormat3(TextRNATransition.apply)
    }
  }

}
