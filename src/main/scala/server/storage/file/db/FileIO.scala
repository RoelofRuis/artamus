package server.storage.file.db

import java.io.{BufferedWriter, File, FileNotFoundException, FileWriter, IOException}

import scala.annotation.tailrec
import scala.io.Source
import scala.util.{Failure, Success, Try}

@deprecated
object FileIO {

  @tailrec
  def readLatest(from: Read): Try[String] = {
    read(from) match {
      case s @ Success(_) => s
      case f @ Failure(_) if from.version.isEmpty => f
      case Failure(_: FileNotFoundException) if from.version.exists(_ > 0) => readLatest(from.copy(version = from.version.map(_ - 1)))
      case f @ Failure(_) => f
    }
  }

  def read(read: Read): Try[String] = {
    Try { Source.fromFile(read.latestPath) }.flatMap { source =>
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
      writer <- Try { new BufferedWriter(new FileWriter(write.latestPath)) }
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
