package storage.impl.file

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, NoSuchFileException, Path, StandardCopyOption}

import storage.api.DbResult

import scala.jdk.StreamConverters._
import scala.util.{Failure, Success, Try}

private[impl] object FileIO {

  def read(path: Path): DbResult[String] = {
    Try { Files.readAllBytes(path) } match {
      case Failure(_: NoSuchFileException) => DbResult.notFound
      case Failure(ex) => DbResult.badData(ex)
      case Success(bytes) => DbResult.found(new String(bytes, StandardCharsets.UTF_8))
    }
  }

  def write(path: Path, data: String): DbResult[Unit] = {
    val res = for {
      _ <- Try { Files.createDirectories(path.getParent) }
      _ <- Try { Files.write(path, data.getBytes(StandardCharsets.UTF_8)) }
    } yield ()

    res match {
      case Failure(ex) => DbResult.badData(ex)
      case Success(_) => DbResult.ok
    }
  }

  def list(path: Path, onlyDirs: Boolean): List[Path] = {
    Try { Files.list(path).toScala(List).filter(path => !onlyDirs || path.toFile.isDirectory) } match {
      case Success(paths) => paths
      case Failure(_) => List()
    }
  }

  def delete(path: Path): DbResult[Unit] = {
    Try {
      Files.delete(path)
    } match {
      case Success(_) => DbResult.ok
      case Failure(ex) => DbResult.badData(ex)
    }
  }

  def move(source: Path, target: Path): DbResult[Unit] = {
    Try {
      Files.move(
        source,
        target,
        StandardCopyOption.ATOMIC_MOVE
      )
    } match {
      case Success(_) => DbResult.ok
      case Failure(ex) => DbResult.badData(ex)
    }
  }

}
