package nl.roelofruis.artamus.tuning

import java.nio.file.{Files, Paths}

import nl.roelofruis.artamus.degree.Model.{Quality, Scale, Tuning}
import nl.roelofruis.artamus.tuning.TuningLoader.FileModel.TextTuning
import spray.json._

import scala.util.Try

object TuningLoader {

  import Parser._

  def loadTuning: Tuning = {
    val textTuning = FileModel.load[TextTuning]("applications/res/tuning.json").get // TODO: remove get

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
      textTuning.textDegrees,
      scaleMap,
      symbolQualityMap
    )
  }

  object FileModel extends DefaultJsonProtocol {

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
      description: String,
      textQualities: List[TextQuality],
      textScales: List[TextScale]
    ) extends MusicPrimitivesParser

    object TextTuning {
      implicit val tuningFormat: JsonFormat[TextTuning] = jsonFormat10(TextTuning.apply)
    }

    def loadList[A : JsonFormat](path: String): Try[List[A]] = load[List[A]](path)

    def load[A : JsonFormat](path: String): Try[A] = {
      Try { new String(Files.readAllBytes(Paths.get(path))).parseJson.convertTo[A] }
    }

  }


}