package server.storage.file.db

import java.io.FileNotFoundException

import javax.inject.{Inject, Singleton}
import server.storage.file.db.FileDB.DataFile
import spray.json.{JsonReader, JsonWriter}

import scala.util.{Failure, Success, Try}

@Singleton
class JsonFileDB @Inject() (
  fileDB: FileDB,
  jsonMarshaller: JsonMarshaller
) {

  def read[A : JsonReader](name: String, orElse: => A): Try[A] = {
    val readResult = for {
      data <- fileDB.read(DataFile(name, "json"))
      obj <- jsonMarshaller.read(data)
    } yield obj

    readResult match {
      case s @ Success(_) => s
      case Failure(_: FileNotFoundException) => Success(orElse)
      case f @ Failure(_) => f
    }
  }

  def write[A : JsonWriter](name: String, data: A): Try[Unit] = {
    for {
      json <- jsonMarshaller.write(data)
    } yield fileDB.write(DataFile(name, "json"), json)
  }

  def update[A : JsonReader : JsonWriter](name: String, default: A)(f: A => A): Try[Unit] = {
    read[A](name, default)
      .map(f)
      .flatMap(write[A](name, _))
  }

}
