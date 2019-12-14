package server.storage.file.db2

import java.io.File
import java.util.concurrent.atomic.AtomicLong

import javax.annotation.concurrent.ThreadSafe
import server.storage.file.db2.DbIO.DbResult
import server.storage.file.db2.DbTransaction.CommitResult

import scala.annotation.tailrec

@ThreadSafe
class FileDb2(
  rootPath: Seq[String]
) extends Db {

  private val writeLock = new Object()
  private val version: AtomicLong = new AtomicLong(0L)

  def newTransaction: UnitOfWork2 = UnitOfWork2(this)

  def commitUnitOfWork(uow: UnitOfWork2): DbTransaction.CommitResult = {
    val changeSet = uow.getChangeSet
    if (changeSet.isEmpty) CommitResult.nothingToCommit
    else {
      writeLock.synchronized {
        val commitVersion = version.getAndIncrement()

        val errors = changeSet.foldRight(List()) { case ((key, data), acc) =>
          FileIO2.write(keyToPath(key, commitVersion), data) match {
            case Right(_) => acc
            case Left(ex) => acc :+ ex
          }
        }

        errors match {
          case list if list.isEmpty =>
            FileIO2.write(keyToPath(Key("_version"), 0), commitVersion.toString) match {
              case Right(_) => CommitResult.success(changeSet.size)
              case Left(ex) => CommitResult.failure(ex)
            }
          case l => CommitResult.failure(l: _*)
        }
      }
    }
  }

  def loadFromFile(key: Key): DbResult[String] = {
    val currentVersion = version.get()

    @tailrec
    def readVersioned(version: Long): DbResult[String] = {
      FileIO2.read(keyToPath(key, version)) match {
        case Left(FileNotFoundException()) if version > 0 => readVersioned(version - 1)
        case l @ Left(FileNotFoundException()) => l
        case _ => _
      }
    }
    readVersioned(currentVersion)
  }

  private def keyToPath(key: Key, version: Long): String = {
    val versionString = version.toString
    val dataFile = s"$versionString.json"
    (rootPath :+ key.name :+ dataFile).mkString(File.separator)
  }

}
