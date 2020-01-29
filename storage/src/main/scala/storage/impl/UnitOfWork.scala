package storage.impl

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

import javax.annotation.concurrent.ThreadSafe
import storage.api.DataModel.DataKey
import storage.api.Transaction.CommitResult
import storage.api.{DataModel, DbIO, DbResult, Transaction}

import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success}

@ThreadSafe
private[impl] final class UnitOfWork private (
  id: UUID,
  private val db: TransactionalDatabase
) extends Transaction with DbIO {

  private val cleanData = new ConcurrentHashMap[DataKey, String]()
  private val dirtyData = new ConcurrentHashMap[DataKey, String]()

  override def readModel[A : DataModel]: DbResult[A] = {
    val model = implicitly[DataModel[A]]
    Option(dirtyData.get(model.key)) orElse Option(cleanData.get(model.key)) match {
      case Some(data) =>
        model.deserialize(data) match {
          case Success(obj) => DbResult.found(obj)
          case Failure(ex) => DbResult.badData(ex)
        }
      case None => db.readModel match {
        case Right(obj) => DbResult.found(obj)
        case l @ Left(_) => l
      }
    }
  }

  override def writeModel[A : DataModel](obj: A): DbResult[Unit] = {
    val model = implicitly[DataModel[A]]
    model.serialize(obj) match {
      case Failure(ex) => DbResult.badData(ex)
      case Success(data) =>
        dirtyData.put(model.key, data)
        DbResult.ok
    }
  }

  override def updateModel[A : DataModel](default: A, f: A => A): DbResult[Unit] = {
    for {
      data <- readModel.ifNotFound(default)
      _ <- writeModel(f(data))
    } yield ()
  }

  override def commit(): CommitResult = db.commitUnitOfWork(this)

  def getChangeSet: Map[DataKey, String] = {
    dirtyData
      .asScala
      .iterator
      .filterNot { case (key, data) => Option(cleanData.get(key)).map(dataHash).contains(dataHash(data)) }
      .toMap
  }

  private val HASHREGEX = """[\n\r\s]+""".r
  private def dataHash(data: String): Int = HASHREGEX.replaceAllIn(data, "").hashCode

}

object UnitOfWork {

  def apply(db: TransactionalDatabase): UnitOfWork = new UnitOfWork(UUID.randomUUID(), db)

}