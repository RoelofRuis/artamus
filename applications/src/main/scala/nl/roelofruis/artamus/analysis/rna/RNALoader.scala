package nl.roelofruis.artamus.analysis.rna

import nl.roelofruis.artamus.analysis.rna.Model._
import nl.roelofruis.artamus.parsing.Model.ParseResult
import nl.roelofruis.artamus.parsing.Parser._
import nl.roelofruis.artamus.tuning.Model.Tuning
import nl.roelofruis.artamus.util.File
import spray.json._

object RNALoader {
  import nl.roelofruis.artamus.analysis.rna.RNALoader.FileModel.{TextRNAFunction, TextRNARules, TextRNASettings, TextRNATransition}

  def loadAnalyser(tuning: Tuning): ParseResult[Analyser] = {
    def parseRulesFiles(files: List[String]): ParseResult[(Set[RNAFunction], Set[RNATransition])] = {
      files.map { file =>
        for {
          textRules <- File.load[TextRNARules](s"applications/data/rna/$file.json")
          functions <- parseFunctions(textRules.functions)
          transitions <- parseTransitions(textRules.transitions)
        } yield (functions, transitions)
      }.invert.map(_.foldLeft((Set[RNAFunction](), Set[RNATransition]())) {
        case ((functionsAcc, transitionsAcc), (functions, transitions)) => (functionsAcc ++ functions, transitionsAcc ++ transitions)
      })
    }

    def parseTransitions(transitions: List[TextRNATransition]): ParseResult[List[RNATransition]] = {
      transitions.map { textTransition =>
        for {
          from <- tuning.parser(textTransition.from).parseDegree
          to <- tuning.parser(textTransition.to).parseDegree
          weight = textTransition.weight.getOrElse(1)
        } yield RNATransition(from, to, weight)
      }.invert
    }

    def parseFunctions(functions: List[TextRNAFunction]): ParseResult[List[RNAFunction]] = {
      functions.map { textFunction =>
        for {
          quality <- tuning.parser(textFunction.quality).parseQuality
          options <- parseOptions(textFunction.options)
          allowEnharmonicEquivalents = textFunction.allowEnharmonicEquivalents.getOrElse(false)
        } yield RNAFunction(quality, options, allowEnharmonicEquivalents)
      }.invert
    }

    def parseOptions(options: List[String]): ParseResult[List[RNAFunctionOption]] =
      options.map { option =>
        val parser = tuning.parser(option)
        for {
          interval <- parser.parseInterval
          _ <- parser.buffer.expectExactly(":", 1)
          scale <- parser.parseScale
          _ <- parser.buffer.expectExactly(":", 1)
          degree <- parser.parseDegree
        } yield RNAFunctionOption(interval, scale, degree)
      }.invert

    for {
      textSettings <- File.load[TextRNASettings]("applications/data/rna/settings.json")
      (functions, transitions) <- parseRulesFiles(textSettings.rulesFiles)
    } yield Analyser(
      tuning,
      RNARules(
        textSettings.numResultsRequired,
        RNAPenalties(
          textSettings.penalties.keyChange,
          textSettings.penalties.unknownTransition
        ),
        functions.toList,
        transitions.toList
      )
    )
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
      weight: Option[Int]
    )

    object TextRNATransition {
      implicit val textRNATransitionFormat: JsonFormat[TextRNATransition] = jsonFormat3(TextRNATransition.apply)
    }
  }

}
