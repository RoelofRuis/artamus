package storage.impl.memory

import java.util.concurrent.ConcurrentHashMap

import javax.annotation.concurrent.ThreadSafe
import storage.api.DbTransaction.CommitResult
import storage.api._
import storage.impl.{CommittableReadableDb, UnitOfWork}

import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success}

@ThreadSafe
private[storage] class InMemoryDb() extends CommittableReadableDb {

  private val state = new ConcurrentHashMap[DataKey, String]()

  def commitUnitOfWork(uow: UnitOfWork): DbTransaction.CommitResult = {
    val changeSet = uow.getChangeSet
    state.putAll(uow.getChangeSet.asJava)
    CommitResult.success(changeSet.size)
  }

  override def readModel[A : Model]: ModelResult[A] = {
    val model = implicitly[Model[A]]
    Option(state.get(model.key)) match {
      case Some(data) => model.deserialize(data) match {
        case Success(obj) => ModelResult.found(obj)
        case Failure(ex) => ModelResult.badData(DataCorruptionException(ex))
      }
      case None => ModelResult.notFound
    }
  }

}
