package server.storage.file.db2

import java.io.{BufferedWriter, File, FileNotFoundException, FileWriter, IOException}

import scala.io.Source
import scala.util.{Failure, Success, Try}

object FileIO2 {

  def read(path: String): DbResult[String] = {
    Try { Source.fromFile(path) } match {
      case Failure(_: FileNotFoundException) => DbResult.failure(FileNotFound())
      case Failure(ex) => DbResult.failure(IOError(ex))
      case Success(source) =>
        try {
          DbResult.success(source.getLines.mkString)
        } catch {
          case ex: IOException => DbResult.failure(IOError(ex))
        } finally {
          source.close()
        }
    }
  }

  def write(path: String, data: String): DbResult[Unit] = {
    val writerAtDir = for {
      _ <- Try { new File(path).mkdirs }
      writer <- Try { new BufferedWriter(new FileWriter(path)) }
    } yield writer

    writerAtDir match {
      case Failure(ex) => DbResult.failure(IOError(ex))
      case Success(writer) =>
        try {
          DbResult.success(writer.write(data))
        } catch {
          case ex: IOException => DbResult.failure(IOError(ex))
        } finally {
          writer.close()
        }
    }
  }

}
