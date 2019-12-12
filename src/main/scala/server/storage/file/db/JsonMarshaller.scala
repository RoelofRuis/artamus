package server.storage.file.db

import spray.json.{JsonWriter, _}

import scala.util.Try

object JsonMarshaller {

  def write[A : JsonWriter](model: A, compact: Boolean): Try[String] = {
    Try {
      if (compact) model.toJson.compactPrint
      else model.toJson.prettyPrint
    }
  }

  def read[A : JsonReader](s: String): Try[A] = {
    Try { s.parseJson.convertTo[A] }
  }

}
