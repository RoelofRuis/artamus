package server.storage

import java.io.File
import java.util.concurrent.atomic.AtomicLong

import javax.annotation.concurrent.ThreadSafe
import server.storage.api.DbTransaction.CommitResult
import server.storage.api.{DataKey, DatabaseError, Db, DbRead, DbResult, DbTransaction, FileNotFound}

import scala.annotation.tailrec

@ThreadSafe
class FileDb(
  rootPath: Seq[String]
) extends Db with DbRead {

  private val writeLock = new Object()
  private val version: AtomicLong = new AtomicLong(0L)

  def newTransaction: UnitOfWork = UnitOfWork(this)

  def commitUnitOfWork(uow: UnitOfWork): DbTransaction.CommitResult = {
    val changeSet = uow.getChangeSet
    if (changeSet.isEmpty) CommitResult.nothingToCommit
    else {
      writeLock.synchronized {
        val commitVersion = version.getAndIncrement()

        val errors = changeSet.foldRight(List[DatabaseError]()) { case ((key, data), acc) =>
          FileIO.write(keyToPath(key, commitVersion), data) match {
            case Right(_) => acc
            case Left(ex) => acc :+ ex
          }
        }

        errors match {
          case list if list.isEmpty =>
            FileIO.write(keyToPath(DataKey("_version"), 0), commitVersion.toString) match {
              case Right(_) => CommitResult.success(changeSet.size)
              case Left(ex) => CommitResult.failure(ex)
            }
          case l => CommitResult.failure(l: _*)
        }
      }
    }
  }

  def readKey(key: DataKey): DbResult[String] = {
    val currentVersion = version.get()

    @tailrec
    def readVersioned(version: Long): DbResult[String] = {
      FileIO.read(keyToPath(key, version)) match {
        case Left(FileNotFound()) if version > 0 => readVersioned(version - 1)
        case l @ Left(FileNotFound()) => l
        case x => x
      }
    }
    readVersioned(currentVersion)
  }

  private def keyToPath(key: DataKey, version: Long): String = {
    val versionString = version.toString
    val dataFile = s"$versionString.json"
    (rootPath :+ key.name :+ dataFile).mkString(File.separator)
  }

}
