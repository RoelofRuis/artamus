package nl.roelofruis.artamus.degree

import java.nio.file.{Files, Paths}

import spray.json._

import scala.util.Try

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
    qualities: List[TextQuality],
    scales: List[TextScale]
  )

  object TextTuning {
    implicit val tuningFormat: JsonFormat[TextTuning] = jsonFormat10(TextTuning.apply)
  }

  def loadList[A : JsonFormat](path: String): Try[List[A]] = load[List[A]](path)

  def load[A : JsonFormat](path: String): Try[A] = {
    Try { new String(Files.readAllBytes(Paths.get(path))).parseJson.convertTo[A] }
  }

}
