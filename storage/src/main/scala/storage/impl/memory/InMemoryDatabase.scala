package storage.impl.memory

import java.util.concurrent.ConcurrentHashMap

import javax.annotation.concurrent.ThreadSafe
import javax.inject.{Inject, Singleton}
import storage.api.DataModel.DataKey
import storage.api.Transaction.CommitResult
import storage.api.{DataModel, DbResult, Transaction}
import storage.impl.{TransactionalDatabase, UnitOfWork}

import scala.collection.JavaConverters._
import scala.util.{Failure, Success}

@ThreadSafe
@Singleton
private[storage] class InMemoryDatabase @Inject() () extends TransactionalDatabase {

  private val state = new ConcurrentHashMap[DataKey, String]()

  def commitUnitOfWork(uow: UnitOfWork): Transaction.CommitResult = {
    val changeSet = uow.getChangeSet
    state.putAll(uow.getChangeSet.asJava)
    CommitResult.success(changeSet.size)
  }

  override def readModel[A : DataModel]: DbResult[A] = {
    val model = implicitly[DataModel[A]]
    Option(state.get(model.key)) match {
      case Some(data) => model.deserialize(data) match {
        case Success(obj) => DbResult.found(obj)
        case Failure(ex) => DbResult.badData(ex)
      }
      case None => DbResult.notFound
    }
  }

}
