package nl.roelofruis.artamus.analysis.rna

import nl.roelofruis.artamus.analysis.rna.Model._
import nl.roelofruis.artamus.degree.Model.Tuning
import nl.roelofruis.artamus.util.File
import spray.json._

object RNALoader {
  import nl.roelofruis.artamus.analysis.rna.RNALoader.FileModel.{TextRNASettings, TextRNARules}

  def loadAnalyser(tuning: Tuning): Analyser = {
    def parseOption(option: String): RNAFunctionOption = {
      val parts = option.split(':')

      RNAFunctionOption(
        tuning.parseInterval.run(parts(0)).value,
        tuning.parseScale.run(parts(1)).value,
        tuning.parseDegree.run(parts(2)).value
      )
    }

    val textSettings = File.load[TextRNASettings]("applications/data/rna/settings.json").get // TODO: remove get

    val (functions, transitions) = textSettings.rulesFiles.map { rulesFile =>
      val textRules = File.load[TextRNARules](s"applications/data/rna/$rulesFile.json").get // TODO: remove get

      val functions = textRules.functions.map { textFunction =>
        RNAFunction(
          tuning.parseQuality.run(textFunction.quality).value,
          textFunction.options.map(parseOption),
          textFunction.allowEnharmonicEquivalents.getOrElse(false)
        )
      }

      val transitions = textRules.transitions.map { textTransition =>
        RNATransition(
          tuning.parseDegree.run(textTransition.from).value,
          tuning.parseDegree.run(textTransition.to).value,
          textTransition.weight
        )
      }
      (functions.toSet, transitions.toSet)
    }.foldRight((Set[RNAFunction](), Set[RNATransition]())) {
      case ((functionsAcc, transitionsAcc), (functions, transitions)) => (functionsAcc ++ functions, transitionsAcc ++ transitions)
    }

    val rules = RNARules(
      textSettings.numResultsRequired,
      RNAPenalties(
        textSettings.penalties.keyChange,
        textSettings.penalties.unknownTransition
      ),
      functions.toList,
      transitions.toList
    )

    Analyser(tuning, rules)
  }

  private object FileModel extends DefaultJsonProtocol {
    final case class TextRNASettings(
      numResultsRequired: Int,
      penalties: TextRNAPenalties,
      rulesFiles: List[String]
    )

    object TextRNASettings {
      implicit val rnaSettingsFormat: JsonFormat[TextRNASettings] = jsonFormat3(TextRNASettings.apply)
    }

    final case class TextRNARules(
      functions: List[TextRNAFunction],
      transitions: List[TextRNATransition]
    )

    object TextRNARules {
      implicit val rnaRulesFormat: JsonFormat[TextRNARules] = jsonFormat2(TextRNARules.apply)
    }

    final case class TextRNAPenalties(
      keyChange: Int,
      unknownTransition: Int
    )

    object TextRNAPenalties {
      implicit val rnaPenaltiesFormat: JsonFormat[TextRNAPenalties] = jsonFormat2(TextRNAPenalties.apply)
    }

    final case class TextRNAFunction(
      quality: String,
      options: List[String],
      allowEnharmonicEquivalents: Option[Boolean]
    )

    object TextRNAFunction {
      implicit val rnafunctionFormat: JsonFormat[TextRNAFunction] = jsonFormat3(TextRNAFunction.apply)
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
