package nl.roelofruis.artamus.application

import nl.roelofruis.artamus.application.Model.{ParseResult, Settings}
import nl.roelofruis.artamus.application.Parser._
import nl.roelofruis.artamus.core.track.Pitched.Key
import nl.roelofruis.artamus.core.track.algorithms.rna.Model._
import spray.json._

object RNALoader {
  import RNALoader.FileModel.{TextRNAInterpretation, TextRNAKeyChange, TextRNARules, TextRNASettings, TextRNATransition}

  def loadRules(tuning: Settings): ParseResult[RNARules] = {
    def parseRulesFiles(files: List[String]): ParseResult[(Set[RNAInterpretation], Set[RNATransition])] = {
      files.map { file =>
        for {
          textRules <- File.load[TextRNARules](s"src/main/resources/data/rna/$file.json")
          interpretations <- parseInterpretations(textRules.interpretations)
          transitions <- parseTransitions(textRules.transitions)
        } yield (interpretations, transitions)
      }.invert.map(_.foldLeft((Set[RNAInterpretation](), Set[RNATransition]())) {
        case ((interpretationsAcc, transitionsAcc), (interpretations, transitions)) => (interpretationsAcc ++ interpretations, transitionsAcc ++ transitions)
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

    def parseInterpretations(interpretations: List[TextRNAInterpretation]): ParseResult[List[RNAInterpretation]] = {
      interpretations.map { textInterpretation =>
        for {
          quality <- tuning.parser(textInterpretation.quality).parseQuality
          options <- parseOptions(textInterpretation.options)
          allowEnharmonicEquivalents = textInterpretation.allowEnharmonicEquivalents.getOrElse(false)
        } yield RNAInterpretation(quality, options, allowEnharmonicEquivalents)
      }.invert
    }

    def parseKeyChanges(keyChanges: List[TextRNAKeyChange]): ParseResult[List[RNAKeyChange]] = {
      keyChanges.map { keyChange =>
        val scaleParser = tuning.parser(keyChange.from)
        val keyParser = tuning.parser(keyChange.to)
        for {
          scaleFrom <- scaleParser.parseScale
          interval <- keyParser.parseInterval
          _ <- keyParser.buffer.expectOne(":")
          scale <- keyParser.parseScale
        } yield RNAKeyChange(scaleFrom, Key(interval, scale), keyChange.weight)
      }.invert
    }

    def parseOptions(options: List[String]): ParseResult[List[RNAInterpretationOption]] =
      options.map { option =>
        val parser = tuning.parser(option)
        for {
          interval <- parser.parseInterval
          _ <- parser.buffer.expectOne(":")
          scale <- parser.parseScale
          _ <- parser.buffer.expectOne(":")
          degree <- parser.parseDegree
        } yield RNAInterpretationOption(interval, scale, degree)
      }.invert

    for {
      textSettings <- File.load[TextRNASettings]("src/main/resources/data/rna/settings.json")
      (interpretations, transitions) <- parseRulesFiles(textSettings.rulesFiles)
      keyChanges <- parseKeyChanges(textSettings.keyChanges)
    } yield RNARules(
      textSettings.maxSolutionsToCheck,
      textSettings.unknownTransitionPenalty,
      textSettings.unknownKeyChangePenalty,
      keyChanges,
      interpretations.toList,
      transitions.toList
    )
  }

  private object FileModel extends DefaultJsonProtocol {
    final case class TextRNASettings(
      maxSolutionsToCheck: Int,
      unknownKeyChangePenalty: Int,
      unknownTransitionPenalty: Int,
      keyChanges: List[TextRNAKeyChange],
      rulesFiles: List[String]
    )

    object TextRNASettings {
      implicit val rnaSettingsFormat: JsonFormat[TextRNASettings] = jsonFormat5(TextRNASettings.apply)
    }

    final case class TextRNAKeyChange(
      from: String,
      to: String,
      weight: Int
    )

    object TextRNAKeyChange {
      implicit val rnaKeyChangeFormat: JsonFormat[TextRNAKeyChange] = jsonFormat3(TextRNAKeyChange.apply)
    }

    final case class TextRNARules(
      interpretations: List[TextRNAInterpretation],
      transitions: List[TextRNATransition]
    )

    object TextRNARules {
      implicit val rnaRulesFormat: JsonFormat[TextRNARules] = jsonFormat2(TextRNARules.apply)
    }

    final case class TextRNAInterpretation(
      quality: String,
      options: List[String],
      allowEnharmonicEquivalents: Option[Boolean]
    )

    object TextRNAInterpretation {
      implicit val rnaInterpretationFormat: JsonFormat[TextRNAInterpretation] = jsonFormat3(TextRNAInterpretation.apply)
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
