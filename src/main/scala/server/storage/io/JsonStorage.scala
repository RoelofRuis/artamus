package server.storage.io

import java.io.File

import spray.json.{JsonReader, JsonWriter}

import scala.util.Try

trait JsonStorage {

  def write[A : JsonWriter](file: File, model: A): Try[Unit]

  def read[A : JsonReader](file: File, newA: => A): Try[A]

  def update[A : JsonReader : JsonWriter](file: File, newA: => A)(f: A => A): Try[Unit]

}
