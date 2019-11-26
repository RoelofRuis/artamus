package server.storage.io

import java.io.{BufferedWriter, File, FileWriter, IOException}

import scala.io.Source
import scala.util.{Failure, Try}

class FileIO() {

  def read(path: String): Try[String] = {
    val bufferedSource = Try { Source.fromFile(path) }
    try {
      bufferedSource.map(_.getLines.mkString)
    } catch {
      case ex: IOException => Failure(ex)
    } finally {
      bufferedSource.map(_.close())
    }
  }

  def write(path: String, contents: String): Try[Unit] = {
    val file = new File(path)
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
