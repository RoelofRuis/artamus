package nl.roelofruis.artamus.util

import java.nio.file.{Files, Paths}

import spray.json.JsonFormat

import scala.util.Try
import spray.json._

object File extends DefaultJsonProtocol {

  def loadList[A : JsonFormat](path: String): Try[List[A]] = load[List[A]](path)

  def load[A : JsonFormat](path: String): Try[A] = {
    Try { new String(Files.readAllBytes(Paths.get(path))).parseJson.convertTo[A] }
  }
}
