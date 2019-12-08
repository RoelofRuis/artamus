package server.storage.file.db

import java.util.concurrent.ConcurrentHashMap

import javax.annotation.concurrent.GuardedBy
import server.storage.file.db.UnitOfWork.{CommitFailed, CommitSuccessful, RollbackSuccessful}

import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success}

class UnitOfWork(
  val initialVersion: Long,
  val versionFile: DataFile,
  val rootPath: Seq[String]
) {

  private val commitLock = new Object()
  @GuardedBy("commitLock") private var version: Long = initialVersion
  private val dataToWrite = new ConcurrentHashMap[DataFile, String]()

  def getLatestWrittenVersion: Long = version - 1

  def getStaged(file: DataFile): Option[String] = Option(dataToWrite.get(file))

  def stage(file: DataFile, data: String): Unit = dataToWrite.put(file, data)

  def commit(): Either[CommitFailed, CommitSuccessful] = commitLock.synchronized {
    val results = dataToWrite.asScala.iterator
      .map { case (file, data) =>
        FileIO.write(Write(file, rootPath, Some(version), data))
      }
      .toList

    if (results.isEmpty) Right(CommitSuccessful(0))
    else {
      dataToWrite.clear()

      results.collect { case Failure(ex) => ex } match {
        case list if list.isEmpty =>
          FileIO.write(Write(versionFile, rootPath, None, version.toString)) match {
            case Success(_) =>
              version += 1
              Right(CommitSuccessful(results.size))
            case Failure(ex) => Left(CommitFailed(Seq(ex)))
          }

        case failures =>
          Left(CommitFailed(failures))
      }
    }
  }

  def rollback(): RollbackSuccessful = commitLock.synchronized {
    val numWrites = dataToWrite.size()
    dataToWrite.clear()
    RollbackSuccessful(numWrites)
  }

}

object UnitOfWork {

  final case class RollbackSuccessful(writes: Int)
  final case class CommitFailed(cause: Seq[Throwable]) extends Throwable
  final case class CommitSuccessful(writes: Int)

}