package server.storage.file.db2

import server.storage.file.db2.DbIO.DbResult
import spray.json.{JsonReader, JsonWriter}
import spray.json._

import scala.util.{Failure, Success, Try}

object JsonDB {

  implicit class JsonDBIO(dbIO: DbIO) {
    private val compact = false

    def read[A : JsonReader](key: Key): DbResult[A] = {
      for {
        data <- dbIO.read(key)
        obj <- JsonMarshaller.read(data)
      } yield obj
    }

    def write[A : JsonWriter](key: Key, data: A): DbResult[Unit] = {
      for {
        json <- JsonMarshaller.write(data, compact)
        write <- dbIO.write(key, json)
      } yield write
    }

  }

  private object JsonMarshaller {

    def write[A : JsonWriter](model: A, compact: Boolean): DbResult[String] = {
      Try {
        if (compact) model.toJson.compactPrint
        else model.toJson.prettyPrint
      } match {
        case Success(s) => DbResult.success(s)
        case Failure(ex) => DbResult.failure(DataCorruptionException(ex))
      }
    }

    def read[A : JsonReader](s: String): DbResult[A] = {
      Try { s.parseJson.convertTo[A] } match {
        case Success(a) => DbResult.success(a)
        case Failure(ex) => DbResult.failure(DataCorruptionException(ex))
      }
    }

  }

}
