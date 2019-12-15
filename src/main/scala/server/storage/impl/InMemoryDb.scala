package server.storage.impl

import java.util.concurrent.ConcurrentHashMap

import javax.annotation.concurrent.ThreadSafe
import server.storage.api.DbTransaction.CommitResult
import server.storage.api._

import scala.jdk.CollectionConverters._

@ThreadSafe
private[storage] class InMemoryDb() extends CommittableReadableDb {

  private val state = new ConcurrentHashMap[DataKey, String]()

  def commitUnitOfWork(uow: UnitOfWork): DbTransaction.CommitResult = {
    val changeSet = uow.getChangeSet
    state.putAll(uow.getChangeSet.asJava)
    CommitResult.success(changeSet.size)
  }

  def readKey(key: DataKey): DbResult[String] = {
    Option(state.get(key)) match {
      case Some(s) => DbResult.success(s)
      case None => DbResult.failure(ResourceNotFound())
    }
  }

}
