package server.storage.file.db

import javax.annotation.concurrent.GuardedBy
import server.storage.file.db.UnitOfWork.{CommitFailed, CommitSuccessful}

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success}

class UnitOfWork(
  initialVersion: Long,
  versionPath: String
) {

  private val guard = new Object()
  @GuardedBy("guard") private var version: Long = initialVersion
  @GuardedBy("guard") private val listBuffer = ListBuffer[Write]()

  def addWrite(write: Write): Unit = guard.synchronized {
    listBuffer.addOne(write)
  }

  def commit(): Either[CommitFailed, CommitSuccessful] = guard.synchronized {
    val results = listBuffer
      .map(FileIO.write)
      .toList

    if (results.isEmpty) Right(CommitSuccessful(0))
    else {
      listBuffer.clear()

      results.collect { case Failure(ex) => ex } match {
        case list if list.isEmpty =>
          FileIO.write(Write(versionPath, version.toString)) match {
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

  def rollback(): Unit = guard.synchronized {
    listBuffer.clear()
  }

}

object UnitOfWork {

  final case class CommitFailed(cause: Seq[Throwable]) extends Throwable
  final case class CommitSuccessful(writes: Int)

}