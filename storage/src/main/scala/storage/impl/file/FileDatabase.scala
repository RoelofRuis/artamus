package storage.impl.file

import java.nio.file.{Path, Paths}

import javax.annotation.concurrent.{GuardedBy, ThreadSafe}
import storage.FileDatabaseConfig
import storage.api.DataTypes.{JSON, Raw}
import storage.api.Transaction.CommitResult
import storage.api.{DbException, DbResult, NotFound, TableModel, Transaction}
import storage.impl.{TransactionalDatabase, UnitOfWork}

import scala.annotation.tailrec
import scala.util.matching.Regex
import scala.util.{Failure, Success, Try}

@ThreadSafe
private[storage] class FileDatabase(config: FileDatabaseConfig) extends TransactionalDatabase {

  private val rootPath = DbPath(config.rootPath)
  private val VersionPath = rootPath.toObject("_version", "0", 0, Raw)

  val initialVersion: Int = FileIO.read(VersionPath) match {
    case Left(_) => 0
    case Right(v) => Try { v.toInt + 1 }.getOrElse(0)
  }

  private val writeLock = new Object()
  @GuardedBy("writeLock") private var version: Int = initialVersion

  def commitUnitOfWork(uow: UnitOfWork): Transaction.CommitResult = {
    val (deletes, updates) = uow.getChangeSet

    deletes.foreach { obj =>
      // TODO: refactor this so changeset is separated!
      FileIO.move(rootPath.toRow(obj.table, obj.id), rootPath.toRow(obj.table, obj.id, Some(version)))
    }

    if (updates.isEmpty) CommitResult.nothingToCommit
    else {
      val res = writeLock.synchronized {
        val errors = updates.foldRight(List[DbException]()) { case (obj, acc) =>
          FileIO.write(rootPath.toObject(obj.id.table, obj.id.id, version, obj.id.dataType), obj.data) match {
            case Right(_) => acc
            case Left(ex) => acc :+ ex
          }
        }

        errors match {
          case list if list.isEmpty =>
            FileIO.write(VersionPath, version.toString) match {
              case Right(_) =>
                version += 1
                CommitResult.success(updates.size)
              case Left(ex) => CommitResult.failure(ex)
            }
          case l => CommitResult.failure(l: _*)
        }
      }
      checkCleanup()
      res
    }
  }

  override def readRow[A, I](id: I)(implicit t: TableModel[A, I]): DbResult[A] = {
    @tailrec
    def readVersioned(version: Int): DbResult[String] = {
      FileIO.read(rootPath.toObject(t.name, t.serializeId(id), version, t.dataType)) match {
        case Left(NotFound()) if version > 0 => readVersioned(version - 1)
        case l @ Left(NotFound()) => l
        case x => x
      }
    }
    val dbResult = readVersioned(version)

    dbResult match {
      case Right(data) => t.deserialize(data) match {
        case Success(obj) => DbResult.found(obj)
        case Failure(ex) => DbResult.ioError(ex)
      }
      case Left(ex) => Left(ex)
    }
  }

  override def readTable[A, I](implicit t: TableModel[A, I]): DbResult[List[A]] = {
    val rows = FileIO.list(rootPath.toTable(t.name), onlyDirs=true)
    @tailrec
    def readVersioned(rowPath: Path, version: Int): DbResult[String] = {
      FileIO.read(DbPath.toObjectFromRowPath(rowPath, version, t.dataType)) match {
        case Left(NotFound()) if version > 0 => readVersioned(rowPath, version - 1)
        case l @ Left(NotFound()) => l
        case x => x
      }
    }
    rows.foldRight(DbResult.found(List[A]())) { case (rowPath, res) =>
      if (! res.isOk) res
      else {
        readVersioned(rowPath, version) match {
          case Right(data) =>
            t.deserialize(data) match {
              case Success(obj) => res.map(_ :+ obj)
              case Failure(ex) => DbResult.ioError(ex)
            }
          case Left(ex) => DbResult.ioError(ex)
        }
      }
    }
  }

  private val OBJECT: Regex = """(\d+)\.([a-z]+)""".r

  private def checkCleanup(): Unit = {
    val filesState = getActiveFiles
    val oldFiles = filesState.foldRight(0) { case ((_, old), acc) => acc + old.size }
    if (oldFiles > config.cleanupThreshold) performCleanup()
  }

  private def performCleanup(): Unit = writeLock.synchronized {
    val res = getActiveFiles.foldRight(List[DbException]()) { case ((latest, old), acc) =>
      val deleteErrors = old.foldRight(List[DbException]()) { case (file, acc) =>
        FileIO.delete(file) match {
          case Right(_) => acc
          case Left(ex) => acc :+ ex
        }
      }
      if (deleteErrors.nonEmpty) acc ++ deleteErrors
      else {
        val newPath = latest.getFileName.toString match {
          case OBJECT(_, ext) => Paths.get(latest.getParent.toString, "0." + ext)
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

  private def getActiveFiles: Seq[(Path, List[Path])] = {
    FileIO
      .list(Paths.get(config.rootPath), onlyDirs=true)
      .flatMap { table => getActiveFilesPerTable(table) }
  }

  private def getActiveFilesPerTable(tablePath: Path): Seq[(Path, List[Path])] = {
    FileIO
      .list(tablePath, onlyDirs=true)
      .flatMap { rowPath =>
        val validFiles = FileIO.list(rowPath, onlyDirs = false)
          .map(_.getFileName.toString)
          .map { case OBJECT(v, ext) => (v.toInt, ext) }
          .filter { case (v, _) => v <= version }
          .sortBy { case (v, _) => v }
          .reverse
          .map { case (v, ext) =>
            val dataType = ext match {
              case "json" => JSON // TODO: move extensions and detection into extension data type
              case "dat" => Raw
              case _ => Raw
            }
            val id = rowPath.getFileName.toString
            val table = rowPath.getParent.getFileName.toString
            rootPath.toObject(table, id, v, dataType)
          }
        if (validFiles.isEmpty) Seq()
        else Seq((validFiles.head, validFiles.tail))
      }
  }

}
