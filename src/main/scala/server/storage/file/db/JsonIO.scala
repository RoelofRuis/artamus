package server.storage.file.db

import java.io.FileNotFoundException

import server.storage.{DBIOException, EntityNotFoundException}
import spray.json.{JsonReader, JsonWriter}

import scala.util.{Failure, Success, Try}

@deprecated
object JsonIO {

  private val compact = false // TODO: move back to props

  implicit class JsonFileDB(fileDB: FileDB) {
    def readByQuery[A : JsonReader, B](query: Query[A, B]): Try[B] = {
      val readResult = for {
        data <- fileDB.read(DataFile(query.name, "json"))
        obj <- JsonMarshaller.read(data)
      } yield query.transform(obj)

      readResult match {
        case Success(Some(a)) => Success(a)
        case Success(None) | Failure(_: FileNotFoundException) => Failure(EntityNotFoundException(query.name))
        case Failure(ex) => Failure(DBIOException(ex))
      }
    }

    def write[A : JsonWriter](name: String, data: A): Try[Unit] = {
      for {
        json <- JsonMarshaller.write(data, compact)
      } yield fileDB.write(DataFile(name, "json"), json)
    }

    def update[A : JsonReader : JsonWriter](name: String, default: => A)(update: A => A): Try[Unit] = {
      readByQuery[A, A](Query(name, x => Some(x)))
        .recover { case EntityNotFoundException(_) => default }
        .map(update)
        .map(write[A](name, _))
    }
  }
}
