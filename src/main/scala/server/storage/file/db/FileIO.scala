package server.storage.file.db

import java.io.{BufferedWriter, FileWriter, IOException}

import scala.io.Source
import scala.util.{Failure, Success, Try}

object FileIO {

  def read(read: Read): Try[String] = {
    Try { Source.fromFile(read.path) }.flatMap { source =>
      try {
        Success(source.getLines.mkString)
      } catch {
        case ex: IOException => Failure(ex)
      } finally {
        source.close()
      }
    }
  }

  def write(write: Write): Try[Unit] = {
    val writer = Try { new BufferedWriter(new FileWriter(write.path)) }
    try {
      writer.map(_.write(write.data))
    } catch {
      case ex: IOException => Failure(ex)
    } finally {
      writer.map(_.close())
    }
  }

}
