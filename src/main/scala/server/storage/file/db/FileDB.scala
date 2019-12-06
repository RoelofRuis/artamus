package server.storage.file.db

import java.io.File
import java.util.concurrent.ConcurrentHashMap

import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import server.storage.TransactionalDB
import server.storage.file.db.FileDB.{CommitFailed, CommitSuccessful, DataFile}

import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success, Try}

class FileDB @Inject() (
  rootPath: Seq[String],
) extends TransactionalDB with LazyLogging {

  // TODO: add versioning so real transactionality can be ensured

  private val dataToWrite = new ConcurrentHashMap[DataFile, Write]()

  def read(file: DataFile): Try[String] = {
    logger.info(s"DB READ  [$file]")
    Option(dataToWrite.get(file)) match {
      case Some(write) => Success(write.data)
      case None => FileIO.read(Read(makePath(file)))
    }
  }

  def write(file: DataFile, data: String): Try[Unit] = {
    logger.info(s"DB WRITE [$file]")
    dataToWrite.put(file, Write(makePath(file), data))
    Success(())
  }

  def commit(): Try[Unit] = commitInternally() match {
    case Right(success) =>
      logger.info(s"DB COMMIT wrote [${success.writes}")
      Success(())

    case Left(failures) =>
      logger.warn(s"DB COMMIT failed", failures)
      Failure(failures)
  }

  def rollback(): Try[Unit] = {
    logger.info(s"DB ROLLBACK")
    dataToWrite.clear()
    Success(())
  }

  private def commitInternally(): Either[CommitFailed, CommitSuccessful] = {
    val results = dataToWrite.values.asScala
      .map(FileIO.write)
      .toList

    if (results.isEmpty) Right(CommitSuccessful(0))
    else {
      dataToWrite.clear()

      results.collect { case Failure(ex) => ex } match {
        case list if list.isEmpty =>
          Right(CommitSuccessful(results.size))

        case failures =>
          Left(CommitFailed(failures))
      }
    }
  }

  private def makePath(description: DataFile): String = {
    (rootPath :+ description.name + "." + description.ext).mkString(File.separator)
  }

}

object FileDB {

  final case class DataFile(name: String, ext: String)

  final case class CommitFailed(cause: Seq[Throwable]) extends Throwable
  final case class CommitSuccessful(writes: Int)

}