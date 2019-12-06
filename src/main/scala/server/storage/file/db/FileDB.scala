package server.storage.file.db

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import server.storage.file.db.FileDB.DataFile

import scala.util.{Failure, Success, Try}

class FileDB @Inject() (
  rootPath: Seq[String],
) extends LazyLogging {

  private val versioningPath = makePath(DataFile("_versioning", "json"))
  private val initialVersion = FileIO.read(Read(versioningPath)).flatMap { s => Try { s.toLong } } match {
    case Success(version) => version
    case Failure(_) => 0 // TODO: add logging here
  }

  private val unitOfWork = new UnitOfWork(initialVersion, versioningPath)

  def read(file: DataFile): Try[String] = {
    logger.info(s"DB READ  [$file]")
    FileIO.read(Read(makePath(file)))
  }

  def write(file: DataFile, data: String): Try[Unit] = {
    logger.info(s"DB WRITE [$file]")
    unitOfWork.addWrite(
      Write(makePath(file), data)
    )
    Success(())
  }

  def commit(): Try[Unit] = {
    logger.info(s"DB COMMIT")
    unitOfWork.commit() match {
      case Right(_) => Success(())
      case Left(failures) => Failure(failures)
    }
  }

  def rollback(): Try[Unit] = {
    logger.info(s"DB ROLLBACK")
    unitOfWork.rollback()
    Success(())
  }

  private def makePath(description: DataFile): String = {
    (rootPath :+ description.name + "." + description.ext).mkString(File.separator)
  }

}

object FileDB {

  final case class DataFile(name: String, ext: String)

}