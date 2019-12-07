package server.storage.file.db

import java.io.{BufferedWriter, File, FileWriter, IOException}

import scala.io.Source
import scala.util.{Failure, Success, Try}

object FileIO {

  def read(path: String): Try[String] = {
    Try { Source.fromFile(path) }.flatMap { source =>
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
    for {
      _ <- Try { new File(write.dir).mkdirs }
      writer <- Try { new BufferedWriter(new FileWriter(write.path)) }
    } yield {
      try {
        writer.write(write.data)
      } catch {
        case ex: IOException => Failure(ex)
      } finally {
        writer.close()
      }
    }

  }

}
