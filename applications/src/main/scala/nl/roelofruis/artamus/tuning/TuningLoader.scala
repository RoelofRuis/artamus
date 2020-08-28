package nl.roelofruis.artamus.tuning

import nl.roelofruis.artamus.core.Model.{Quality, Scale}
import nl.roelofruis.artamus.parsing.Model.{ParseResult, PitchedPrimitives}
import nl.roelofruis.artamus.parsing.MusicPrimitivesParser
import nl.roelofruis.artamus.tuning.Model.Tuning
import nl.roelofruis.artamus.util.File
import spray.json._
import nl.roelofruis.artamus.parsing.Parser._

object TuningLoader {
  import nl.roelofruis.artamus.tuning.TuningLoader.FileModel.TextTuning

  def loadTuning: ParseResult[Tuning] = {
    for {
      textTuning <- File.load[TextTuning]("applications/data/tuning.json")
      qualityMap <- parseQualityMap(textTuning)
      scaleMap = buildScaleMap(textTuning)
    } yield Tuning(
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
      qualityMap
    )
  }

  private def parseQualityMap(textTuning: TextTuning): ParseResult[Map[String, Quality]] =
    textTuning.textQualities.map { quality =>
      val parser = MusicPrimitivesParser(quality.intervals, textTuning)
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
      implicit val tuningFormat: JsonFormat[TextTuning] = jsonFormat11(TextTuning.apply)
    }
  }
}
