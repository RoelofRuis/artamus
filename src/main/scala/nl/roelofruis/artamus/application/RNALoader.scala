package nl.roelofruis.artamus.application

import nl.roelofruis.artamus.application.Model.{ParseResult, PitchedObjects, PitchedPrimitives, Settings}
import nl.roelofruis.artamus.application.Utils._
import ObjectParsers._
import fastparse._
import fastparse.SingleLineWhitespace._
import nl.roelofruis.artamus.core.track.Pitched.{Degree, Key, PitchDescriptor, Scale}
import nl.roelofruis.artamus.core.track.algorithms.rna.Model._
import spray.json._

object RNALoader {
  import RNALoader.FileModel.{TextRNAInterpretation, TextRNAKeyChange, TextRNARules, TextRNASettings, TextRNATransition, TextRNAInterpretationOption}

  def loadRules(tuning: Settings): ParseResult[RNARules] = {
    def parseRules(textRules: TextRNARules): ParseResult[(List[RNAInterpretation], List[RNATransition])] = {
      for {
        interpretations <- parseInterpretations(textRules.interpretations)
        transitions <- parseTransitions(textRules.transitions)
      } yield (interpretations, transitions)
    }

    def parseTransitions(transitions: List[TextRNATransition]): ParseResult[List[RNATransition]] = {
      transitions.map { textTransition =>
        for {
          from <- doParse(textTransition.from, tuning.degree(_))
          to <- textTransition.to.map(doParse(_, tuning.degree(_))).toList.invert
          weight = textTransition.weight.getOrElse(1)
        } yield to.map(RNATransition(from, _, weight))
      }.invert.map(_.flatten)
    }

    def parseInterpretations(interpretations: List[TextRNAInterpretation]): ParseResult[List[RNAInterpretation]] = {
      interpretations.map { textInterpretation =>
        for {
          qualityGroup  <- doParse(textInterpretation.qualityGroup, tuning.qualityGroup(_))
          options       <- parseOptions(textInterpretation.options)
          allowEnharmonicEquivalents = textInterpretation.allowEnharmonicEquivalents.getOrElse(false)
        } yield RNAInterpretation(qualityGroup, options, allowEnharmonicEquivalents)
      }.invert
    }

    def parseKeyChanges(keyChanges: List[TextRNAKeyChange]): ParseResult[List[RNAKeyChange]] = {
      keyChanges.map { keyChange =>
        for {
          scaleFrom         <- doParse(keyChange.from, tuning.scale(_))
          (interval, scale) <- doParse(keyChange.to, tuning.intervalAndScale(_))
        } yield RNAKeyChange(scaleFrom, Key(interval, scale), keyChange.weight)
      }.invert
    }

    def parseOptions(options: List[TextRNAInterpretationOption]): ParseResult[List[RNAInterpretationOption]] =
      options.map { option =>
        doParse(option.option, tuning.intervalScaleDegree(_))
          .map{ case (interval, scale, degree) => RNAInterpretationOption(interval, scale, degree) }
      }.invert

    for {
      textSettings <- File.load[TextRNASettings]("src/main/resources/data/rna_settings.json")
      (interpretations, transitions) <- parseRules(textSettings.rules)
      keyChanges <- parseKeyChanges(textSettings.keyChanges)
    } yield RNARules(
      textSettings.maxSolutionsToCheck,
      textSettings.unknownTransitionPenalty,
      textSettings.unknownKeyChangePenalty,
      keyChanges,
      interpretations,
      transitions,
    )
  }

  private implicit class FromPitchedObjectsAdvanced(pp: PitchedPrimitives with PitchedObjects) {
    def intervalAndScale[_ : P]: P[(PitchDescriptor, Scale)] = P(pp.interval ~ ":" ~ pp.scale)
    def intervalScaleDegree[_ : P]: P[(PitchDescriptor, Scale, Degree)] = P(
      pp.interval ~~ ":" ~~ pp.scale ~~ ":" ~~ pp.degree ~~ End
    )
  }

  private object FileModel extends DefaultJsonProtocol {
    final case class TextRNASettings(
      maxSolutionsToCheck: Int,
      unknownKeyChangePenalty: Int,
      unknownTransitionPenalty: Int,
      keyChanges: List[TextRNAKeyChange],
      rules: TextRNARules,
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
      qualityGroup: String,
      options: List[TextRNAInterpretationOption],
      allowEnharmonicEquivalents: Option[Boolean]
    )

    object TextRNAInterpretation {
      implicit val rnaInterpretationFormat: JsonFormat[TextRNAInterpretation] = jsonFormat3(TextRNAInterpretation.apply)
    }

    final case class TextRNAInterpretationOption(
      option: String,
      rule: String
    )

    object TextRNAInterpretationOption {
      implicit val rnaInterpretationOptionFormat: JsonFormat[TextRNAInterpretationOption] = jsonFormat2(TextRNAInterpretationOption.apply)
    }

    final case class TextRNATransition(
      from: String,
      to: Seq[String],
      weight: Option[Int]
    )

    object TextRNATransition {
      implicit val textRNATransitionFormat: JsonFormat[TextRNATransition] = jsonFormat3(TextRNATransition.apply)
    }
  }

}
