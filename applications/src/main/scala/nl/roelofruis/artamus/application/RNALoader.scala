package nl.roelofruis.artamus.application

import nl.roelofruis.artamus.application.Model.{ParseResult, Settings}
import nl.roelofruis.artamus.application.Parser._
import nl.roelofruis.artamus.core.Pitched.Key
import nl.roelofruis.artamus.core.analysis.rna.Analyser
import nl.roelofruis.artamus.core.analysis.rna.Model._
import spray.json._

object RNALoader {
  import RNALoader.FileModel.{TextRNAFunction, TextRNAKeyChange, TextRNARules, TextRNASettings, TextRNATransition}

  def loadAnalyser(tuning: Settings): ParseResult[Analyser] = {
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

    def parseKeyChanges(keyChanges: List[TextRNAKeyChange]): ParseResult[List[RNAKeyChange]] = {
      keyChanges.map { keyChange =>
        val parser = tuning.parser(keyChange.to)
        for {
          interval <- parser.parseInterval
          _ <- parser.buffer.expectOne(":")
          scale <- parser.parseScale
        } yield RNAKeyChange(Key(interval, scale), keyChange.weight)
      }.invert
    }

    def parseOptions(options: List[String]): ParseResult[List[RNAFunctionOption]] =
      options.map { option =>
        val parser = tuning.parser(option)
        for {
          interval <- parser.parseInterval
          _ <- parser.buffer.expectOne(":")
          scale <- parser.parseScale
          _ <- parser.buffer.expectOne(":")
          degree <- parser.parseDegree
        } yield RNAFunctionOption(interval, scale, degree)
      }.invert

    for {
      textSettings <- File.load[TextRNASettings]("applications/data/rna/settings.json")
      (functions, transitions) <- parseRulesFiles(textSettings.rulesFiles)
      keyChanges <- parseKeyChanges(textSettings.keyChanges)
    } yield Analyser(
      tuning,
      RNARules(
        textSettings.numResultsRequired,
        textSettings.unknownTransitionPenalty,
        textSettings.unknownKeyChangePenalty,
        keyChanges,
        functions.toList,
        transitions.toList
      )
    )
  }

  private object FileModel extends DefaultJsonProtocol {
    final case class TextRNASettings(
      numResultsRequired: Int,
      unknownKeyChangePenalty: Int,
      unknownTransitionPenalty: Int,
      keyChanges: List[TextRNAKeyChange],
      rulesFiles: List[String]
    )

    object TextRNASettings {
      implicit val rnaSettingsFormat: JsonFormat[TextRNASettings] = jsonFormat5(TextRNASettings.apply)
    }

    final case class TextRNAKeyChange(
      to: String,
      weight: Int
    )

    object TextRNAKeyChange {
      implicit val rnaKeyChangeFormat: JsonFormat[TextRNAKeyChange] = jsonFormat2(TextRNAKeyChange.apply)
    }

    final case class TextRNARules(
      functions: List[TextRNAFunction],
      transitions: List[TextRNATransition]
    )

    object TextRNARules {
      implicit val rnaRulesFormat: JsonFormat[TextRNARules] = jsonFormat2(TextRNARules.apply)
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
