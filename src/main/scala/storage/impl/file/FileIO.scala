package storage.impl.file

import java.io._
import java.nio.file.{Files, Paths, StandardCopyOption}

import storage.api.DbResult

import scala.io.Source
import scala.util.{Failure, Success, Try}

private[impl] object FileIO {

  def read(path: String): DbResult[String] = {
    Try { Source.fromFile(path) } match {
      case Failure(_: FileNotFoundException) => DbResult.notFound
      case Failure(ex) => DbResult.ioError(ex)
      case Success(source) =>
        try {
          DbResult.success(source.getLines.mkString)
        } catch {
          case ex: IOException => DbResult.ioError(ex)
        } finally {
          source.close()
        }
    }
  }

  def write(path: String, data: String): DbResult[Unit] = {
    val writerAtDir = for {
      _ <- Try { new File(path).getParentFile.mkdirs }
      writer <- Try { new BufferedWriter(new FileWriter(path)) }
    } yield writer

    writerAtDir match {
      case Failure(ex) => DbResult.ioError(ex)
      case Success(writer) =>
        try {
          DbResult.success(writer.write(data))
        } catch {
          case ex: IOException => DbResult.ioError(ex)
        } finally {
          writer.close()
        }
    }
  }

  def list(path: String, onlyDirs: Boolean): List[String] = {
    Try { new File(path) } match {
      case Failure(_) => List()
      case Success(file) =>
        file
          .listFiles()
          .filter(file => !onlyDirs || file.isDirectory)
          .map(_.getName)
          .toList
    }
  }


  def delete(path: String): DbResult[Unit] = {
    Try {
      Files.delete(Paths.get(path))
    } match {
      case Success(_) => DbResult.done
      case Failure(ex) => DbResult.ioError(ex)
    }
  }

  def move(path: String, target: String): DbResult[Unit] = {
    Try {
      Files.move(
        Paths.get(path),
        Paths.get(target),
        StandardCopyOption.ATOMIC_MOVE
      )
    } match {
      case Success(_) => DbResult.done
      case Failure(ex) => DbResult.ioError(ex)
    }
  }

}
