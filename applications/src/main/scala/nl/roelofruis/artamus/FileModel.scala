package nl.roelofruis.artamus

import java.nio.file.{Files, Paths}

import spray.json._

import scala.util.Try

object FileModel extends DefaultJsonProtocol {

  final case class TextDegree(
    text: String,
    description: String
  )

  final case class TextExpansionRule(
    base: String,
    expansion: String
  )

  object Protocol {
    implicit val degreeFormat: JsonFormat[TextDegree] = jsonFormat2(TextDegree.apply)
    implicit val expansionRuleFormat: JsonFormat[TextExpansionRule] = jsonFormat2(TextExpansionRule.apply)
  }

  def loadList[A : JsonFormat](path: String): Try[List[A]] = load[List[A]](path)

  def load[A : JsonFormat](path: String): Try[A] = {
    Try { new String(Files.readAllBytes(Paths.get(path))).parseJson.convertTo[A] }
  }

}
