package storage.impl.file

import java.nio.file.{Path, Paths}

import javax.annotation.concurrent.{GuardedBy, ThreadSafe}
import javax.inject.{Inject, Named, Singleton}
import storage.api.Transaction.CommitResult
import storage.api.DataModel.{DataKey, JSON, Raw}
import storage.api._
import storage.impl.{TransactionalDatabase, UnitOfWork}

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}
import scala.util.matching.Regex

@ThreadSafe
@Singleton
private[storage] class FileDatabase @Inject() (
  @Named("db-root-path") rootPath: String,
  @Named("cleanup-threshold") cleanupThreshold: Int
) extends TransactionalDatabase {

  private val VersionPath = keyToPath(DataKey("_version", Raw), 0)

  val initialVersion: Int = FileIO.read(VersionPath) match {
    case Left(_) => 0
    case Right(v) => Try { v.toInt + 1 }.getOrElse(0)
  }

  private val writeLock = new Object()
  @GuardedBy("writeLock") private var version: Int = initialVersion

  def commitUnitOfWork(uow: UnitOfWork): Transaction.CommitResult = {
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
            FileIO.write(VersionPath, version.toString) match {
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

  override def readModel[A : DataModel]: DbResult[A] = {
    val model = implicitly[DataModel[A]]
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

  private val FILE: Regex = """(\d+)\.([a-z]+)""".r

  private def checkCleanup(): Unit = {
    val filesState = getActiveFilesPerKey
    val oldFiles = filesState.foldRight(0) { case ((_, old), acc) => acc + old.size }
    if (oldFiles > cleanupThreshold) performCleanup()
  }

  private def performCleanup(): Unit = writeLock.synchronized {
    val res = getActiveFilesPerKey.foldRight(List[DbException]()) { case ((latest, old), acc) =>
      val deleteErrors = old.foldRight(List[DbException]()) { case (file, acc) =>
        FileIO.delete(file) match {
          case Right(_) => acc
          case Left(ex) => acc :+ ex
        }
      }
      if (deleteErrors.nonEmpty) acc ++ deleteErrors
      else {
        val newPath = latest.getFileName.toString match {
          case FILE(_, ext) => Paths.get(latest.getParent.toString, "0." + ext)
        }
        FileIO.move(latest, newPath) match {
          case Right(_) => List()
          case Left(ex) => List(ex)
        }
      }
    }

    if (res.isEmpty) {
      FileIO.write(VersionPath, "0") match {
        case Right(_) => version = 0
        case Left(_) =>
      }
    }
  }

  private def getActiveFilesPerKey: Seq[(Path, List[Path])] = {
    FileIO
      .list(Paths.get(rootPath), onlyDirs=true)
      .flatMap { root =>
        val validFiles = FileIO.list(root, onlyDirs = false)
          .map(_.getFileName.toString)
          .map { case FILE(v, ext) => (v.toInt, ext) }
          .filter { case (v, _) => v <= version }
          .sortBy { case (v, _) => v }
          .reverse
          .map { case (v, ext) =>
            val dataType = ext match {
              case "json" => JSON
              case "dat" => Raw
              case _ => Raw
            }
            keyToPath(DataKey(root.getFileName.toString, dataType), v)
          }
        if (validFiles.isEmpty) Seq()
        else Seq((validFiles.head, validFiles.tail))
      }
  }

  private def keyToPath(key: DataKey, version: Int): Path = {
    val versionString = version.toString
    val extension = key.dataType match {
      case JSON => "json"
      case Raw => "dat"
    }
    val dataFile = s"$versionString.$extension"
    Paths.get(rootPath, key.name, dataFile)
  }

}
