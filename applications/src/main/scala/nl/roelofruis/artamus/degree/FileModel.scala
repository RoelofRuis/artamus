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

  final case class TextTuning(
    pitchClassSequence: List[Int],
    noteNames: List[String],
    degreeNames: List[String],
    textSharp: String,
    textFlat: String,
    numPitchClasses: Int,
    description: String,
    scales: List[TextScale],
  )

  object TextTuning {
    implicit val tuningFormat: JsonFormat[TextTuning] = jsonFormat8(TextTuning.apply)
  }

  def loadList[A : JsonFormat](path: String): Try[List[A]] = load[List[A]](path)

  def load[A : JsonFormat](path: String): Try[A] = {
    Try { new String(Files.readAllBytes(Paths.get(path))).parseJson.convertTo[A] }
  }

}
