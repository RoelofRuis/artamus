package nl.roelofruis.artamus.tuning

import nl.roelofruis.artamus.core.Model.{Quality, Scale}
import nl.roelofruis.artamus.tuning.Model.Tuning
import nl.roelofruis.artamus.util.File
import spray.json._

object TuningLoader {
  import Parser._
  import nl.roelofruis.artamus.tuning.TuningLoader.FileModel.TextTuning

  def loadTuning: Tuning = {
    val textTuning = File.load[TextTuning]("applications/data/tuning.json").get // TODO: remove get

    val symbolQualityMap = textTuning.textQualities.map { quality =>
      val parsed = parseArray(textTuning.parseInterval).run(quality.intervals).value
      (quality.symbol, Quality(parsed))
    }.toMap

    val scaleMap = textTuning.textScales.map { scale =>
      (scale.name, Scale(scale.pitchClassSequence))
    }.toMap

    Tuning(
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
      symbolQualityMap
    )
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
      description: String,
      textQualities: List[TextQuality],
      textScales: List[TextScale]
    ) extends MusicPrimitivesParser

    object TextTuning {
      implicit val tuningFormat: JsonFormat[TextTuning] = jsonFormat12(TextTuning.apply)
    }
  }
}
