package nl.roelofruis.artamus.application

import java.nio.file.{Files, Paths}

import spray.json.{JsonFormat, _}

import scala.util.Try

object File extends DefaultJsonProtocol {
  def load[A : JsonFormat](path: String): Try[A] = {
    Try { new String(Files.readAllBytes(Paths.get(path))).parseJson.convertTo[A] }
  }
}
