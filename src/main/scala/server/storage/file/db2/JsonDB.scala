package server.storage.file.db2

import spray.json.{JsonReader, JsonWriter, _}

import scala.util.{Failure, Success, Try}

object JsonDB {

  implicit class JsonDbRead(dbRead: DbRead) {
    def read[A : JsonReader](key: DataKey): DbResult[A] = {
      for {
        data <- dbRead.readKey(key)
        obj <- JsonMarshaller.read(data)
      } yield obj
    }
  }

  implicit class JsonDbWrite(dbWrite: DbWrite) {
    private val compact = false

    def write[A : JsonWriter](key: DataKey, data: A): DbResult[Unit] = {
      for {
        json <- JsonMarshaller.write(data, compact)
        write <- dbWrite.writeKey(key, json)
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
