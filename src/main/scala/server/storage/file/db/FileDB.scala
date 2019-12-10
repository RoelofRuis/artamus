package server.storage.file.db

import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import server.storage.TransactionalDB
import server.storage.file.db.UnitOfWork.CommitResult

import scala.util.{Failure, Success, Try}

class FileDB @Inject() (
  rootPath: Seq[String],
) extends TransactionalDB with LazyLogging {

  private val versionFile = DataFile("_version", "json")
  private val initialVersion = FileIO.read(Read(versionFile, None, rootPath)) match {
    case Success(data) => Try { data.toLong } match {
      case Success(lastVersion) if lastVersion >= 1 =>
        logger.debug(s"DB version at [$lastVersion]")
        lastVersion

      case _ =>
        logger.warn("Invalid version, starting from [1]")
        1L
    }
    case Failure(_) =>
      logger.info("No initial version found, starting from [1]")
      1L
  }

  private val uow: UnitOfWork = new UnitOfWork(initialVersion, versionFile, rootPath)

  def read(file: DataFile): Try[String] = {
    logger.debug(s"DB READ  [$file]")
    uow.get(file) match {
      case Some(data) => Success(data)
      case None => loadFromFile(file)
    }
  }

  private def loadFromFile(file: DataFile): Try[String] = {
    val readResult = FileIO.readLatest(Read(file, Some(uow.getLatestWrittenVersion), rootPath))
    if (readResult.isSuccess) uow.registerClean(file, readResult.get)
    readResult
  }

  def write(file: DataFile, data: String): Try[Unit] = {
    logger.debug(s"DB WRITE [$file]")
    Success(uow.registerDirty(file, data))
  }

  def commit(): Try[Unit] = {
    val result = uow.commit()

    result match {
      case CommitResult(Nil, 0, 0) =>
        logger.info(s"DB nothing to commit")
        Success(())
      case CommitResult(Nil, 0, s) =>
        logger.info(s"DB COMMIT > skipped [$s]")
        Success(())
      case CommitResult(Nil, w, 0) =>
        logger.info(s"DB COMMIT > wrote [$w]")
        Success(())
      case CommitResult(Nil, w, s) =>
        logger.info(s"DB COMMIT > wrote [$w] > skipped [$s]")
        Success(())
      case CommitResult(errors, _, _) =>
        logger.warn(s"DB COMMIT failed")
        errors.foreach { cause => logger.error("commit failed", cause) }
        Failure(errors.head)
    }
  }

  def rollback(): Try[Unit] = {
    val result = uow.rollback()
    if (result.writes == 0) logger.debug(s"DB nothing to roll back")
    else logger.info(s"DB ROLLBACK rolled back [${result.writes}]")
    Success(())
  }

}
