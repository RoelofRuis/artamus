package server.storage.file.db

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import javax.annotation.concurrent.GuardedBy
import javax.inject.Inject
import server.storage.TransactionalDB
import server.storage.file.db.FileDB.{CommitFailed, CommitSuccessful, DataFile}

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

class FileDB @Inject() (
  rootPath: Seq[String],
) extends TransactionalDB with LazyLogging {

  private val VERSION_PATH = makePath(DataFile("_versioning", "json"))

  private val guard = new Object()

  @GuardedBy("guard") private var version: Long = 0L
  @GuardedBy("guard") private val listBuffer = ListBuffer[Write]()

  initVersion()

  def read(file: DataFile): Try[String] = {
    logger.info(s"DB READ  [$file]")
    FileIO.read(Read(makePath(file)))
  }

  def write(file: DataFile, data: String): Try[Unit] = {
    logger.info(s"DB WRITE [$file]")
    guard.synchronized { listBuffer.addOne(Write(makePath(file), data)) }
    Success(())
  }

  def commit(): Try[Unit] = {
    val res = guard.synchronized {
      val results = listBuffer
        .map(FileIO.write)
        .toList

      if (results.isEmpty) Right(CommitSuccessful(0))
      else {
        listBuffer.clear()

        results.collect { case Failure(ex) => ex } match {
          case list if list.isEmpty =>
            FileIO.write(Write(VERSION_PATH, version.toString)) match {
              case Success(()) =>
                version += 1
                Right(CommitSuccessful(results.size))
              case Failure(ex) => Left(CommitFailed(Seq(ex)))
            }

          case failures =>
            Left(CommitFailed(failures))
        }
      }
    }

    res match {
      case Right(success) =>
        logger.info(s"DB COMMIT wrote [${success.writes}")
        Success(())

      case Left(failures) =>
        logger.warn(s"DB COMMIT failed", failures)
        Failure(failures)
    }
  }

  def rollback(): Try[Unit] = {
    logger.info(s"DB ROLLBACK")
    guard.synchronized { listBuffer.clear() }
    Success(())
  }

  private def makePath(description: DataFile): String = {
    (rootPath :+ description.name + "." + description.ext).mkString(File.separator)
  }

  private def initVersion(): Unit = guard.synchronized {
    FileIO.read(Read(VERSION_PATH)).flatMap { s => Try { s.toLong } } match {
      case Success(v) => version = v
      case Failure(_) => version = 0L
    }
  }

}

object FileDB {

  final case class DataFile(name: String, ext: String)

  final case class CommitFailed(cause: Seq[Throwable]) extends Throwable
  final case class CommitSuccessful(writes: Int)

}