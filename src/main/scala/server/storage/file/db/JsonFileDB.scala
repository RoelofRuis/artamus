package server.storage.file.db

import java.io.FileNotFoundException

import javax.inject.{Inject, Singleton}
import server.storage.{DBIOException, EntityNotFoundException}
import spray.json.{JsonReader, JsonWriter}

import scala.util.{Failure, Success, Try}

@Singleton
class JsonFileDB @Inject() (
  fileDB: FileDB,
  jsonMarshaller: JsonMarshaller
) {

  def readByQuery[A : JsonReader, B](query: Query[A, B]): Try[B] = {
    val readResult = for {
      data <- fileDB.read(DataFile(query.name, "json"))
      obj <- jsonMarshaller.read(data)
    } yield query.transform(obj)

    readResult match {
      case Success(Some(a)) => Success(a)
      case Success(None) | Failure(_: FileNotFoundException) => Failure(EntityNotFoundException(query.name))
      case Failure(ex) => Failure(DBIOException(ex))
    }
  }

  def write[A : JsonWriter](name: String, data: A): Try[Unit] = {
    for {
      json <- jsonMarshaller.write(data)
    } yield fileDB.write(DataFile(name, "json"), json)
  }

  def update[A : JsonReader : JsonWriter](name: String)(f: PartialFunction[Option[A], A]): Try[Unit] = {
    readByQuery[A, A](Query(name, x => Some(x)))
      .map(x => Some(x))
      .recover { case EntityNotFoundException(_) => None }
      .collect(f)
      .flatMap(write[A](name, _))
  }

}
