package nl.roelofruis.artamus.application

import nl.roelofruis.artamus.application.Model.{ParseResult, Settings}
import nl.roelofruis.artamus.application.Parser._
import nl.roelofruis.artamus.core.track.algorithms.functional.Model.{FunctionalAnalysisRules, IntervalDescription, QualityTag, TagReduction}
import spray.json._

object FunctionalAnalysisLoader {
  import nl.roelofruis.artamus.application.FunctionalAnalysisLoader.FileModel.{TextFunctionalSettings, TextTagReduction}

  def loadRules(tuning: Settings): ParseResult[FunctionalAnalysisRules] = {
    def parseTagReductions(reduction: Seq[TextTagReduction]): ParseResult[List[TagReduction]] = {
      reduction.toList.map { reduction =>
        for {
          intervals <- parseIntervalDescription(reduction.intervals)
          possibleTags = reduction.tags.map(QualityTag)
        } yield TagReduction(
          intervals,
          possibleTags
        )
      }.invert
    }

    def parseIntervalDescription(intervals: String): ParseResult[List[IntervalDescription]] = {
      intervals.split(" ").toList.map{ s =>
        val parser = tuning.parser(s)
        for {
          shouldNotContain <- parser.buffer.hasResult("!")
          interval <- parser.parseInterval
        } yield IntervalDescription(!shouldNotContain, interval)
      }.invert
    }

    for {
      textRules <- File.load[TextFunctionalSettings](s"src/main/resources/data/functional/settings.json")
      tagReductions <- parseTagReductions(textRules.tagReductions)
    } yield FunctionalAnalysisRules(
      tagReductions
    )
  }

  private object FileModel extends DefaultJsonProtocol {

    final case class TextFunctionalSettings(
      tagReductions: Seq[TextTagReduction],
    )

    object TextFunctionalSettings {
      implicit val functionalSettingsFormat: JsonFormat[TextFunctionalSettings] = jsonFormat1(TextFunctionalSettings.apply)
    }

    final case class TextTagReduction(
      intervals: String,
      tags: Seq[String]
    )

    object TextTagReduction {
      implicit val tagReductionFormat: JsonFormat[TextTagReduction] = jsonFormat2(TextTagReduction.apply)
    }
  }

}
