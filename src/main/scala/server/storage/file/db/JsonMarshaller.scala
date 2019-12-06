package server.storage.file.db

import javax.inject.Inject
import spray.json.{JsonWriter, _}

import scala.util.Try

class JsonMarshaller @Inject() (compact: Boolean) {

  def write[A : JsonWriter](model: A): Try[String] = {
    Try {
      if (compact) model.toJson.compactPrint
      else model.toJson.prettyPrint
    }
  }

  def read[A : JsonReader](s: String): Try[A] = {
    Try { s.parseJson.convertTo[A] }
  }

}
