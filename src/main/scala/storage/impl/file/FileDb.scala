package storage.impl.file

import java.io.File

import javax.annotation.concurrent.{GuardedBy, ThreadSafe}
import storage.api.DbTransaction.CommitResult
import storage.api._
import storage.impl.{CommittableReadableDb, UnitOfWork}

import scala.annotation.tailrec
import scala.util.Try
import scala.util.matching.Regex

@ThreadSafe
private[storage] class FileDb(
  rootPath: Seq[String],
  cleanupThreshold: Int,
) extends CommittableReadableDb {

  private val VersionPath = keyToPath(DataKey("_version"), 0)

  val initialVersion: Int = FileIO.read(VersionPath) match {
    case Left(_) => 0
    case Right(v) => Try { v.toInt + 1 }.getOrElse(0)
  }

  private val writeLock = new Object()
  @GuardedBy("writeLock") private var version: Int = initialVersion

  def commitUnitOfWork(uow: UnitOfWork): DbTransaction.CommitResult = {
    val changeSet = uow.getChangeSet
    if (changeSet.isEmpty) CommitResult.nothingToCommit
    else {
      val res = writeLock.synchronized {
        val errors = changeSet.foldRight(List[DatabaseError]()) { case ((key, data), acc) =>
          FileIO.write(keyToPath(key, version), data) match {
            case Right(_) => acc
            case Left(ex) => acc :+ ex
          }
        }

        errors match {
          case list if list.isEmpty =>
            FileIO.write(keyToPath(DataKey("_version"), 0), version.toString) match {
              case Right(_) =>
                version += 1
                CommitResult.success(changeSet.size)
              case Left(ex) => CommitResult.failure(ex)
            }
          case l => CommitResult.failure(l: _*)
        }
      }
      checkCleanup()
      res
    }
  }

  def readKey(key: DataKey): DbResult[String] = {
    @tailrec
    def readVersioned(version: Int): DbResult[String] = {
      FileIO.read(keyToPath(key, version)) match {
        case Left(ResourceNotFound()) if version > 0 => readVersioned(version - 1)
        case l @ Left(ResourceNotFound()) => l
        case x => x
      }
    }
    readVersioned(version)
  }

  // TODO: clean up cleanup logic ;-)
  private def checkCleanup(): Unit = {
    val filesState = getActiveFilesPerKey
    val oldFiles = filesState.foldRight(0) { case ((_, _, old), acc) => acc + old.size }
    if (oldFiles > cleanupThreshold) performCleanup()
  }

  private def performCleanup(): Unit = writeLock.synchronized {
    val res = getActiveFilesPerKey.foldRight(List[DatabaseError]()) { case ((key, latest, old), acc) =>
      val deleteErrors = old.foldRight(List[DatabaseError]()) { case (file, acc) =>
        FileIO.delete(file) match {
          case Right(_) => acc
          case Left(ex) => acc :+ ex
        }
      }
      if (deleteErrors.nonEmpty) acc ++ deleteErrors
      else {
        FileIO.move(latest, keyToPath(key, 0)) match {
          case Right(_) => List()
          case Left(ex) => List(ex)
        }
      }
    }

    if (res.isEmpty) {
      FileIO.write(keyToPath(DataKey("_version"), 0), "0") match {
        case Right(_) => version = 0
        case Left(_) => // TODO: proper return value
      }
    } else {
      res.foreach(println)
    }
  }

  private val FILE: Regex = """(\d+)\.[a-z]+""".r
  private def getActiveFilesPerKey: Seq[(DataKey, String, List[String])] = {
    FileIO
      .list(rootPath.mkString(File.separator), onlyDirs=true)
      .map { root =>
        val validFiles = FileIO.list((rootPath :+ root).mkString(File.separator), onlyDirs = false)
          .map { case FILE(v) => v.toInt }
          .filter(_ <= version)
          .sorted
          .reverse
          .map(i => keyToPath(DataKey(root), i))
        (DataKey(root), validFiles.head, validFiles.tail)
      }
  }

  private def keyToPath(key: DataKey, version: Int): String = {
    val versionString = version.toString
    val dataFile = s"$versionString.json"
    (rootPath :+ key.name :+ dataFile).mkString(File.separator)
  }

}
