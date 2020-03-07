package storage.impl

import java.util.concurrent.ConcurrentHashMap

import javax.annotation.concurrent.ThreadSafe
import storage.api.TableModel.{ObjectId, StorableObject}
import storage.api.Transaction.CommitResult
import storage.api.{DbResult, DbIO, TableModel, Transaction}

import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success}

@ThreadSafe
private[impl] final class UnitOfWork private (
  private val db: TransactionalDatabase
) extends Transaction with DbIO {

  private val dirtyObjects = new ConcurrentHashMap[ObjectId, StorableObject]()
  private val deletedObjects = new ConcurrentHashMap[ObjectId, Unit]()

  override def readRow[A, I](id: I)(implicit t: TableModel[A, I]): DbResult[A] = {
    val objectId = ObjectId(t.name, t.serializeId(id), t.dataType)
    if (deletedObjects.contains(objectId)) DbResult.notFound
    else {
      Option(dirtyObjects.get(objectId)) match {
        case Some(storable) =>
          t.deserialize(storable.data) match {
            case Success(obj) => DbResult.found(obj)
            case Failure(ex) => DbResult.ioError(ex)
          }
        case None => db.readRow(id) match {
          case Right(obj) => DbResult.found(obj)
          case l @ Left(_) => l
        }
      }
    }
  }

  override def readTable[A, I](implicit t: TableModel[A, I]): DbResult[List[A]] = {
    db.readTable match {
      case Right(rows) =>
        rows.foldRight(DbResult.found(List[A]())) { case (row, result) =>
          if (! result.isOk) result
          else {
            val id = ObjectId(t.name, t.serializeId(t.objectId(row)), t.dataType)
            Option(dirtyObjects.get(id)) match {
              case None => result.map(_ :+ row)
              case Some(dirtyObj) =>
                t.deserialize(dirtyObj.data) match {
                  case Failure(ex) => DbResult.ioError(ex)
                  case Success(dirty) => result.map(_ :+ dirty)
                }
            }
          }
        }
      case l @ Left(_) => l
    }
  }

  override def writeRow[A, I](obj: A)(implicit t: TableModel[A, I]): DbResult[Unit] = {
    val res = for {
      objectData <- t.serialize(obj)
    } yield StorableObject(ObjectId(t.name, t.serializeId(t.objectId(obj)), t.dataType), objectData)

    res match {
      case Failure(ex) => DbResult.ioError(ex)
      case Success(obj) =>
        dirtyObjects.put(obj.id, obj)
        DbResult.ok
    }
  }

  override def deleteRow[A, I](id: I)(implicit t: TableModel[A, I]): DbResult[Unit] = {
    deletedObjects.put(ObjectId(t.name, t.serializeId(id), t.dataType), ())
    DbResult.ok
  }

  override def commit(): CommitResult = db.commitUnitOfWork(this)

  def getChangeSet: (List[ObjectId], List[StorableObject]) = (
    deletedObjects.asScala.keys.toList,
    dirtyObjects.asScala.values.toList
  )

}

object UnitOfWork {

  def apply(db: TransactionalDatabase): UnitOfWork = new UnitOfWork(db)

}