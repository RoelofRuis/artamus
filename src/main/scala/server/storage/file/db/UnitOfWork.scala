package server.storage.file.db

import java.util.concurrent.ConcurrentHashMap

import javax.annotation.concurrent.GuardedBy
import server.storage.file.db.UnitOfWork.{CommitResult, RollbackResult}

import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success}

class UnitOfWork(
  val initialVersion: Long,
  val versionFile: DataFile,
  val rootPath: Seq[String]
) {

  private val commitLock = new Object()
  @GuardedBy("commitLock") private var version: Long = initialVersion
  private val cleanData = new ConcurrentHashMap[DataFile, String]()
  private val dirtyData = new ConcurrentHashMap[DataFile, String]()

  def getLatestWrittenVersion: Long = version - 1

  def get(file: DataFile): Option[String] = {
    Option(dirtyData.get(file)) orElse Option(cleanData.get(file))
  }

  def registerClean(file: DataFile, data: String): Unit = cleanData.put(file, data)

  def registerDirty(file: DataFile, data: String): Unit = dirtyData.put(file, data)

  def commit(): CommitResult = commitLock.synchronized {
    val results = dirtyData.asScala.iterator
      .foldRight(CommitResult()) { case ((file, data), result) =>
        if (Option(cleanData.get(file)).map(dataHash).contains(dataHash(data))) result.countSkipped
        else {
          FileIO.write(Write(file, rootPath, Some(version), data)) match {
            case Success(()) => result.countWrite
            case Failure(ex) => result.withError(ex)
          }
        }
      }

    if (results.hadNothingToWrite) results
    else {
      dirtyData.clear()
      cleanData.clear()

      results.errors match {
        case list if list.isEmpty =>
          FileIO.write(Write(versionFile, rootPath, None, version.toString)) match {
            case Failure(ex) => results.withError(ex)
            case Success(_) =>
              version += 1
              results
          }
        case _ => results
      }
    }
  }

  def rollback(): RollbackResult = commitLock.synchronized {
    val numWrites = dirtyData.size()
    dirtyData.clear()
    cleanData.clear()
    RollbackResult(numWrites)
  }

}

object UnitOfWork {

  final case class CommitResult(errors: Seq[Throwable] = Seq(), writes: Int = 0, skipped: Int = 0) {
    def isSuccess: Boolean = errors.isEmpty
    def hadNothingToWrite: Boolean = isSuccess && writes == 0
    def hadNothingToCommit: Boolean = hadNothingToWrite && skipped == 0

    def withError(error: Throwable): CommitResult = copy(errors :+ error)
    def countWrite: CommitResult = copy(writes = writes + 1)
    def countSkipped: CommitResult = copy(skipped = skipped + 1)
  }

  final case class RollbackResult(writes: Int)

}