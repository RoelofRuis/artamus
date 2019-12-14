package server.storage.file.db2

import java.io.File
import java.util.concurrent.atomic.AtomicLong

import javax.annotation.concurrent.ThreadSafe
import server.storage.file.db2.DbTransaction.CommitResult

import scala.annotation.tailrec

@ThreadSafe
class FileDb2(
  rootPath: Seq[String]
) extends Db with DbRead {

  private val writeLock = new Object()
  private val version: AtomicLong = new AtomicLong(0L)

  def newTransaction: UnitOfWork2 = UnitOfWork2(this)

  def commitUnitOfWork(uow: UnitOfWork2): DbTransaction.CommitResult = {
    val changeSet = uow.getChangeSet
    if (changeSet.isEmpty) CommitResult.nothingToCommit
    else {
      writeLock.synchronized {
        val commitVersion = version.getAndIncrement()

        val errors = changeSet.foldRight(List[DatabaseError]()) { case ((key, data), acc) =>
          FileIO2.write(keyToPath(key, commitVersion), data) match {
            case Right(_) => acc
            case Left(ex) => acc :+ ex
          }
        }

        errors match {
          case list if list.isEmpty =>
            FileIO2.write(keyToPath(DataKey("_version"), 0), commitVersion.toString) match {
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
      FileIO2.read(keyToPath(key, version)) match {
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
