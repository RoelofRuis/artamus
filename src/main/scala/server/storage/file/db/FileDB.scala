package server.storage.file.db

import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import server.storage.TransactionalDB

import scala.util.{Failure, Success, Try}

class FileDB @Inject() (
  rootPath: Seq[String],
) extends TransactionalDB with LazyLogging {

  private val versionFile = DataFile("_version", "json")
  private val initialVersion = FileIO.read(Read(versionFile, None, rootPath)) match {
    case Success(data) => Try { data.toLong } match {
      case Success(lastVersion) if lastVersion >= 1 =>
        logger.info(s"DB version at [$lastVersion]")
        lastVersion

      case _ =>
        logger.info("Invalid version, starting from [1]")
        1L
    }
    case Failure(_) =>
      logger.info("No initial version found, starting from [1]")
      1L
  }

  private val uow: UnitOfWork = new UnitOfWork(initialVersion, versionFile, rootPath)

  def read(file: DataFile): Try[String] = {
    logger.info(s"DB READ  [$file]")
    uow.getStaged(file) match {
      case Some(data) => Success(data)
      case None => FileIO.readLatest(Read(file, Some(uow.getLatestWrittenVersion), rootPath))
    }
  }

  def write(file: DataFile, data: String): Try[Unit] = {
    logger.info(s"DB WRITE [$file]")
    Success(uow.stage(file, data))
  }

  def commit(): Try[Unit] =
    uow.commit() match {
      case Right(success) =>
        logger.info(s"DB COMMIT wrote [${success.writes}]")
        Success(())

      case Left(failures) =>
        logger.warn(s"DB COMMIT failed")
        failures.cause.foreach { cause =>
          logger.error("commit failed", cause)
        }
        Failure(failures)
    }

  def rollback(): Try[Unit] = {
    uow.rollback()
    logger.info(s"DB ROLLBACK")
    Success(())
  }

}
