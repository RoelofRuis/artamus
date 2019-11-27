package server.storage.io

import java.io._

import scala.io.Source
import scala.util.{Failure, Success, Try}

class FileIO() {

  def read(file: File): Try[String] = {
    Try { Source.fromFile(file) }.flatMap { source =>
      try {
        Success(source.getLines.mkString)
      } catch {
        case ex: IOException => Failure(ex)
      } finally {
        source.close()
      }
    }
  }

  def write(file: File, contents: String): Try[Unit] = {
    val writer = Try { new BufferedWriter(new FileWriter(file)) }
    try {
      writer.map(_.write(contents))
    } catch {
      case ex: IOException => Failure(ex)
    } finally {
      writer.map(_.close())
    }
  }

}
