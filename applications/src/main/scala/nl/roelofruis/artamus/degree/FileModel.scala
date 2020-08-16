package nl.roelofruis.artamus.degree

import java.nio.file.{Files, Paths}

import spray.json._

import scala.util.Try

object FileModel extends DefaultJsonProtocol {

  final case class TextScale(
    tuning: String,
    name: String,
    pitchClassSequence: List[Int]
  )

  object TextScale {
    implicit val scaleFormat: JsonFormat[TextScale] = jsonFormat3(TextScale.apply)
  }

  final case class TuningDescriptor(
    tuning: String,
    pitchClass: Int,
    step: Int,
  )

  object TuningDescriptor {
    implicit val descriptorFormat: JsonFormat[TuningDescriptor] = jsonFormat3(TuningDescriptor.apply)
  }

  final case class TextDegree(
    text: String,
    tuningDescriptor: TuningDescriptor,
  )

  object TextDegree {
    implicit val degreeFormat: JsonFormat[TextDegree] = jsonFormat2(TextDegree.apply)
  }

  final case class TextExpansionRule(
    base: String,
    expansion: String
  )

  object TextExpansionRule {
    implicit val expansionRuleFormat: JsonFormat[TextExpansionRule] = jsonFormat2(TextExpansionRule.apply)
  }

  final case class TextTuning(
    pitchClassSequence: List[Int],
    noteNames: List[String],
    textSharp: String,
    textFlat: String,
    numPitchClasses: Int,
    id: String,
    description: String,
    scales: List[TextScale]
  )

  object TextTuning {
    implicit val tuningFormat: JsonFormat[TextTuning] = jsonFormat8(TextTuning.apply)
  }

  def loadList[A : JsonFormat](path: String): Try[List[A]] = load[List[A]](path)

  def load[A : JsonFormat](path: String): Try[A] = {
    Try { new String(Files.readAllBytes(Paths.get(path))).parseJson.convertTo[A] }
  }

}
