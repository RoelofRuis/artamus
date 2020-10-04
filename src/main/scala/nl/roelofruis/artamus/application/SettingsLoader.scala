package nl.roelofruis.artamus.application

import nl.roelofruis.artamus.application.Model._
import nl.roelofruis.artamus.application.Parser._
import nl.roelofruis.artamus.core.track.Pitched.{PitchDescriptor, Quality, QualityGroup, Scale}
import spray.json._

object SettingsLoader {
  import nl.roelofruis.artamus.application.SettingsLoader.FileModel.TextTuning

  def loadTuning: ParseResult[Settings] = {
    for {
      textTuning <- File.load[TextTuning]("src/main/resources/data/music_settings.json")
      qualityMap <- parseQualityMap(textTuning)
      qualityGroupMap <- parseQualityGroupMap(textTuning, qualityMap)
      scaleMap = buildScaleMap(textTuning)
      defaultMetre <- textTuning.parser(textTuning.defaultMetre).parseMetre
      partialSettings = buildPartialSettings(textTuning, scaleMap, qualityMap, qualityGroupMap)
      defaultKey <- partialSettings.parser(textTuning.defaultKey).parseKey
    } yield Settings(
      textTuning.pitchClassSequence,
      textTuning.numPitchClasses,
      textTuning.textNotes,
      textTuning.textIntervals,
      textTuning.textSharp,
      textTuning.textFlat,
      textTuning.textBarLine,
      textTuning.textRepeatMark,
      textTuning.textDegrees,
      scaleMap,
      qualityMap,
      qualityGroupMap,
      defaultMetre,
      defaultKey,
    )
  }

  private def parseQualityGroupMap(textTuning: TextTuning, qualityMap: Map[String, Quality]): ParseResult[Map[String, QualityGroup]] = {
    textTuning.qualityGroups.map { qualityGroup =>
      qualityGroup.intervalMatchers.split(" ").toList.map { s =>
        val parser = textTuning.parser(s)
        for {
          isOptional     <- parser.buffer.hasResult("~")
          shouldMatchAny <- parser.buffer.hasResult("?")
          interval       <- parser.parseInterval
        } yield {
          if (shouldMatchAny) AnyIntervalOnStep(isOptional, interval.step)
          else ExactInterval(isOptional, interval)
        }
      }
        .invert
        .map(qualityGroup.symbol -> groupQualities(_, qualityMap))
    }
      .invert
      .map(_.toMap)
  }

  private def parseQualityMap(textTuning: TextTuning): ParseResult[Map[String, Quality]] =
    textTuning.qualities.map { quality =>
      val parser = textTuning.parser(quality.intervals)
      parser.parseList(parser.parseInterval, " ").map { intervals =>
        (quality.symbol, Quality(intervals))
      }
    }
      .invert
      .map(_.toMap)

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
      textTuning.textNotes,
      textTuning.textIntervals,
      textTuning.textSharp,
      textTuning.textFlat,
      textTuning.textBarLine,
      textTuning.textRepeatMark,
      textTuning.textDegrees,
      scaleMap,
      qualityMap,
      qualityGroupMap
    )
  }

  private def groupQualities(matchers: List[IntervalMatcher], qualities: Map[String, Quality]): QualityGroup = {
    val matches = qualities.values.toList.map { quality =>
      val score = matchers.foldLeft(0) { case (acc, descr) =>
        val (optional, contains) = descr match {
          case AnyIntervalOnStep(optional, step) => (optional, quality.intervals.map(_.step).contains(step))
          case ExactInterval(optional, interval) => (optional, quality.intervals.contains(interval))
        }
        if (contains) acc + 1
        else if (optional) acc
        else -1
      }
      (score, quality)
    }
      .filter { case (score, _) => score >= 0 }

    val maxScore = matches.maxBy { case (score, _) => score }._1
    QualityGroup(matches.filter { case (score, _) => score == maxScore }.map { case (_, quality) => quality })
  }

  trait IntervalMatcher
  final case class ExactInterval(optional: Boolean, interval: PitchDescriptor) extends IntervalMatcher
  final case class AnyIntervalOnStep(optional: Boolean, step: Int) extends IntervalMatcher

  private final case class PartialSettings(
    pitchClassSequence: List[Int],
    numPitchClasses: Int,
    textNotes: List[String],
    textIntervals: List[String],
    textSharp: String,
    textFlat: String,
    textBarLine: String,
    textRepeatMark: String,
    textDegrees: List[String],
    scaleMap: Map[String, Scale],
    qualityMap: Map[String, Quality],
    qualityGroupMap: Map[String, QualityGroup]
  ) extends PitchedPrimitives with PitchedObjects with TemporalSettings

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
      textNotes: List[String],
      textDegrees: List[String],
      textIntervals: List[String],
      textSharp: String,
      textFlat: String,
      textBarLine: String,
      textRepeatMark: String,
      qualities: List[TextQuality],
      qualityGroups: List[TextQualityGroup],
      scales: List[TextScale]
    ) extends PitchedPrimitives

    object TextTuning {
      implicit val tuningFormat: JsonFormat[TextTuning] = jsonFormat14(TextTuning.apply)
    }
  }
}
