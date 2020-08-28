package nl.roelofruis.artamus.settings

import nl.roelofruis.artamus.core.Pitched.{Quality, Scale}
import nl.roelofruis.artamus.parsing.Model.{ParseResult, PitchedPrimitives}
import nl.roelofruis.artamus.settings.Model.Settings
import nl.roelofruis.artamus.util.File
import spray.json._
import nl.roelofruis.artamus.parsing.Parser._

object SettingsLoader {
  import nl.roelofruis.artamus.settings.SettingsLoader.FileModel.TextTuning

  def loadTuning: ParseResult[Settings] = {
    for {
      textTuning <- File.load[TextTuning]("applications/data/system_settings.json")
      qualityMap <- parseQualityMap(textTuning)
      defaultMetre <- textTuning.parser(textTuning.defaultMetre).parseMetre
      scaleMap = buildScaleMap(textTuning)
    } yield Settings(
      textTuning.pitchClassSequence,
      textTuning.numPitchClasses,
      textTuning.textNotes,
      textTuning.textIntervals,
      textTuning.textSharp,
      textTuning.textFlat,
      textTuning.textBarLine,
      textTuning.textBeatIndication,
      textTuning.textDegrees,
      scaleMap,
      qualityMap,
      defaultMetre,
    )
  }

  private def parseQualityMap(textTuning: TextTuning): ParseResult[Map[String, Quality]] =
    textTuning.textQualities.map { quality =>
      val parser = textTuning.parser(quality.intervals)
      parser.parseList(parser.parseInterval, " ").map { intervals =>
        (quality.symbol, Quality(intervals))
      }
    }
      .invert
      .map(_.toMap)

  private def buildScaleMap(textTuning: TextTuning): Map[String, Scale] = {
    textTuning.textScales.map { scale => (scale.name, Scale(scale.pitchClassSequence)) }.toMap
  }

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

    final case class TextTuning(
      pitchClassSequence: List[Int],
      numPitchClasses: Int,
      defaultMetre: String,
      textNotes: List[String],
      textDegrees: List[String],
      textIntervals: List[String],
      textSharp: String,
      textFlat: String,
      textBarLine: String,
      textBeatIndication: String,
      textQualities: List[TextQuality],
      textScales: List[TextScale]
    ) extends PitchedPrimitives

    object TextTuning {
      implicit val tuningFormat: JsonFormat[TextTuning] = jsonFormat12(TextTuning.apply)
    }
  }
}
