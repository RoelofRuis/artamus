package server.storage.io

import javax.inject.Inject
import spray.json.{JsonReader, JsonWriter, _}

import scala.util.{Failure, Success, Try}

class JsonIO @Inject() (fileIO: FileIO, compact: Boolean = true) {

  def write[A : JsonWriter](path: String, model: A): Try[Unit] = {
    val json = if (compact) model.toJson.compactPrint else model.toJson.prettyPrint
    fileIO.write(path, json)
  }

  def read[A : JsonReader](path: String): Try[A] = {
    fileIO.read(path) match {
      case Success(content) => Try { content.parseJson.convertTo[A] }
      case Failure(ex) => Failure(ex)
    }
  }

}
