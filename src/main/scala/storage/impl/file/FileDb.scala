package storage.impl.file

import java.nio.file.{Path, Paths}

import javax.annotation.concurrent.{GuardedBy, ThreadSafe}
import javax.inject.{Inject, Named, Singleton}
import storage.api.DbTransaction.CommitResult
import storage.api._
import storage.impl.{CommittableReadableDb, UnitOfWork}

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}
import scala.util.matching.Regex

@ThreadSafe
@Singleton
private[storage] class FileDb @Inject() (
  @Named("db-root-path") rootPath: String,
  @Named("cleanup-threshold") cleanupThreshold: Int
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
        val errors = changeSet.foldRight(List[DbException]()) { case ((key, data), acc) =>
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

  override def readModel[A : Model]: DbResult[A] = {
    val model = implicitly[Model[A]]
    @tailrec
    def readVersioned(version: Int): DbResult[String] = {
      FileIO.read(keyToPath(model.key, version)) match {
        case Left(NotFound()) if version > 0 => readVersioned(version - 1)
        case l @ Left(NotFound()) => l
        case x => x
      }
    }
    val dbResult = readVersioned(version)

    dbResult match {
      case Right(data) => model.deserialize(data) match {
        case Success(obj) => DbResult.found(obj)
        case Failure(ex) => DbResult.badData(ex)
      }
      case Left(ex) => Left(ex)
    }
  }

  private def checkCleanup(): Unit = {
    val filesState = getActiveFilesPerKey
    val oldFiles = filesState.foldRight(0) { case ((_, _, old), acc) => acc + old.size }
    if (oldFiles > cleanupThreshold) performCleanup()
  }

  private def performCleanup(): Unit = writeLock.synchronized {
    val res = getActiveFilesPerKey.foldRight(List[DbException]()) { case ((key, latest, old), acc) =>
      val deleteErrors = old.foldRight(List[DbException]()) { case (file, acc) =>
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
        case Left(_) =>
      }
    }
  }

  private val FILE: Regex = """(\d+)\.[a-z]+""".r
  private def getActiveFilesPerKey: Seq[(DataKey, Path, List[Path])] = {
    FileIO
      .list(Paths.get(rootPath), onlyDirs=true)
      .flatMap { root =>
        val validFiles = FileIO.list(root, onlyDirs = false)
          .map(_.getFileName.toString)
          .map { case FILE(v) => v.toInt }
          .filter(_ <= version)
          .sorted
          .reverse
          .map(i => keyToPath(DataKey(root.getFileName.toString), i))
        if (validFiles.isEmpty) Seq()
        else Seq((DataKey(root.getFileName.toString), validFiles.head, validFiles.tail))
      }
  }

  private def keyToPath(key: DataKey, version: Int): Path = {
    val versionString = version.toString
    val dataFile = s"$versionString.dat"
    Paths.get(rootPath, key.name, dataFile)
  }

}
