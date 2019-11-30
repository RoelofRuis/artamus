package server.storage.io

import java.io.{File, FileNotFoundException}

import javax.inject.Inject
import spray.json.{JsonReader, JsonWriter, _}

import scala.util.{Failure, Success, Try}

private [storage] class BasicJsonStorage @Inject() (fileIO: FileIO, compact: Boolean = true) extends JsonStorage {

  def write[A : JsonWriter](file: File, model: A): Try[Unit] = {
    val json = if (compact) model.toJson.compactPrint else model.toJson.prettyPrint
    fileIO.write(file, json)
  }

  def read[A : JsonReader](file: File, newA: => A): Try[A] = {
    fileIO.read(file) match {
      case Failure(_: FileNotFoundException) => Success(newA)
      case Failure(ex) => Failure(ex)
      case Success(content) => Try { content.parseJson.convertTo[A] }
    }
  }

  def update[A : JsonReader : JsonWriter](file: File, newA: => A)(f: A => A): Try[Unit] = {
    read[A](file, newA)
      .map(f)
      .flatMap(write[A](file, _))
  }

}
