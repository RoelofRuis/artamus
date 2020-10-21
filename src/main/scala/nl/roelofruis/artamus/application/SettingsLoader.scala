package nl.roelofruis.artamus.application

import fastparse.SingleLineWhitespace._
import fastparse._
import nl.roelofruis.artamus.application.Model._
import nl.roelofruis.artamus.application.ObjectParsers._
import nl.roelofruis.artamus.application.Utils._
import nl.roelofruis.artamus.core.track.Pitched.{PitchDescriptor, Quality, QualityGroup, Scale}
import nl.roelofruis.artamus.core.track.algorithms.PitchedMaths.TuningDefinition
import spray.json._

object SettingsLoader {
  import nl.roelofruis.artamus.application.SettingsLoader.FileModel.TextTuning

  def loadTuning: ParseResult[Settings] = {
    for {
      textTuning <- File.load[TextTuning]("src/main/resources/data/music_settings.json")
      qualityMap <- parseQualityMap(textTuning)
      qualityGroupMap <- parseQualityGroupMap(textTuning, qualityMap)
      scaleMap = buildScaleMap(textTuning)
      defaultMetre <- doParse(textTuning.defaultMetre, textTuning.metre(_))
      partialSettings = buildPartialSettings(textTuning, scaleMap, qualityMap, qualityGroupMap)
      defaultKey <- doParse(textTuning.defaultKey, partialSettings.key(_), true)
    } yield Settings(
      textTuning.pitchClassSequence,
      textTuning.numPitchClasses,
      scaleMap,
      qualityMap,
      qualityGroupMap,
      defaultMetre,
      defaultKey,
    )
  }

  private implicit class FromPitchedPrimitivesAdvanced(tuning: TuningDefinition) {
    def intervalMatcher[_: P]: P[IntervalMatcher] = P((exists("~") ~ exists("?") ~ tuning.interval).map {
      case (isOptional, shouldMatchAny, interval) =>
        if (shouldMatchAny) AnyIntervalOnStep(isOptional, interval.step)
        else ExactInterval(isOptional, interval)
    })

    def intervalMatchers[_ : P]: P[Seq[IntervalMatcher]] = P(intervalMatcher.rep(1))

    def intervals[_ : P]: P[Seq[PitchDescriptor]] = P(tuning.interval.rep(1))
  }

  private def parseQualityGroupMap(textTuning: TextTuning, qualityMap: Map[String, Quality]): ParseResult[Map[String, QualityGroup]] = {
    textTuning.qualityGroups.map { qualityGroup =>
      doParse(qualityGroup.intervalMatchers, textTuning.intervalMatchers(_))
        .map(qualityGroup.symbol -> groupQualities(_, qualityMap))
    }
      .invert
      .map(_.toMap)
  }

  private def parseQualityMap(textTuning: TextTuning): ParseResult[Map[String, Quality]] = {
    textTuning.qualities.map { quality =>
      doParse(quality.intervals, textTuning.intervals(_))
        .map(quality.symbol -> Quality(_))
    }
      .invert
      .map(_.toMap)
  }

  private def buildScaleMap(textTuning: TextTuning): Map[String, Scale] = {
    textTuning.scales.map { scale => (scale.name, Scale(scale.pitchClassSequence)) }.toMap
  }

  private def buildPartialSettings(
    textTuning: TextTuning,
    scaleMap: Map[String, Scale],
    qualityMap: Map[String, Quality],
    qualityGroupMap: Map[String, QualityGroup]
  ): PartialSettings = {
    PartialSettings(
      textTuning.pitchClassSequence,
      textTuning.numPitchClasses,
      scaleMap,
      qualityMap,
      qualityGroupMap
    )
  }

  private def groupQualities(matchers: Seq[IntervalMatcher], qualities: Map[String, Quality]): QualityGroup = {
    val matches = qualities.values.toList.flatMap { quality =>
      val valid = matchers.foldLeft(true) { case (acc, descr) =>
        val stepMap = quality.intervals.map(pd => pd.step -> pd).toMap
        val valid = descr match {
          case AnyIntervalOnStep(optional, step) =>
            if (optional) true
            else stepMap.isDefinedAt(step)
          case ExactInterval(optional, interval) =>
            if (optional) ! stepMap.isDefinedAt(interval.step) || quality.intervals.contains(interval)
            else quality.intervals.contains(interval)
        }
        acc && valid
      }
      if (valid) Some(quality)
      else None
    }

    QualityGroup(matches)
  }

  trait IntervalMatcher
  final case class ExactInterval(optional: Boolean, interval: PitchDescriptor) extends IntervalMatcher
  final case class AnyIntervalOnStep(optional: Boolean, step: Int) extends IntervalMatcher

  private final case class PartialSettings(
    pitchClassSequence: List[Int],
    numPitchClasses: Int,
    scaleMap: Map[String, Scale],
    qualityMap: Map[String, Quality],
    qualityGroupMap: Map[String, QualityGroup]
  ) extends TuningDefinition with PitchedObjects

  private object FileModel extends DefaultJsonProtocol {

    final case class TextScale(
      name: String,
      pitchClassSequence: List[Int]
    )

    object TextScale {
      implicit val scaleFormat: JsonFormat[TextScale] = jsonFormat2(TextScale.apply)
    }

    final case class TextQuality(
      name: String,
      symbol: String,
      intervals: String
    )

    object TextQuality {
      implicit val qualityFormat: JsonFormat[TextQuality] = jsonFormat3(TextQuality.apply)
    }

    final case class TextQualityGroup(
      name: String,
      symbol: String,
      intervalMatchers: String
    )

    object TextQualityGroup {
      implicit val qualityGroupFormat: JsonFormat[TextQualityGroup] = jsonFormat3(TextQualityGroup.apply)
    }

    final case class TextTuning(
      pitchClassSequence: List[Int],
      numPitchClasses: Int,
      defaultMetre: String,
      defaultKey: String,
      qualities: List[TextQuality],
      qualityGroups: List[TextQualityGroup],
      scales: List[TextScale]
    ) extends TuningDefinition

    object TextTuning {
      implicit val tuningFormat: JsonFormat[TextTuning] = jsonFormat7(TextTuning.apply)
    }
  }
}
