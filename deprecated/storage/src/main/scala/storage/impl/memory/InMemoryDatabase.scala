package storage.impl.memory

import java.util.concurrent.ConcurrentHashMap

import javax.annotation.concurrent.ThreadSafe
import storage.api.Transaction.CommitResult
import storage.api.{DbResult, DataModel, Transaction}
import storage.impl.{TransactionalDatabase, UnitOfWork}

import scala.util.{Failure, Success}

@ThreadSafe
private[storage] class InMemoryDatabase() extends TransactionalDatabase {

  private val tableState = new ConcurrentHashMap[String, Map[String, String]]()

  def commitUnitOfWork(uow: UnitOfWork): Transaction.CommitResult = {
    val (deletes, updates) = uow.getChangeSet
    deletes.foreach { id =>
      tableState.put(id.table, tableState.getOrDefault(id.table, Map()).removed(id.id))
    }

    updates.foreach { obj =>
      tableState.put(obj.id.table, tableState.getOrDefault(obj.id.table, Map()).updated(obj.id.id, obj.data))
    }
    CommitResult.success(updates.size + deletes.size)
  }

  override def readRow[A, I](id: I)(implicit t: DataModel[A, I]): DbResult[A] = {
    Option(tableState.get(t.name)) match {
      case Some(tableData) =>
        tableData.get(t.serializeId(id)) match {
          case Some(row) =>
            t.deserialize(row) match {
              case Success(obj) => DbResult.found(obj)
              case Failure(ex) => DbResult.ioError(ex)
            }
          case None => DbResult.notFound
        }
      case None => DbResult.notFound
    }
  }

  override def readTable[A, I](implicit t: DataModel[A, I]): DbResult[List[A]] = {
    Option(tableState.get(t.name)) match {
      case Some(tableData) =>
        tableData
          .values
          .foldLeft(DbResult.found(List[A]())) { case (res, data) =>
            if (! res.isOk) res
            else {
              t.deserialize(data) match {
                case Success(obj) => res.map(_ :+ obj)
                case Failure(ex) => DbResult.ioError(ex)
              }
            }
          }
      case None => DbResult.found(List())
    }
  }

}
