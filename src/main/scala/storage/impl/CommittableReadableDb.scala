package storage.impl

import server.storage.api.{DbIO, DbWithRead}
import storage.api.DbTransaction

private[impl] trait CommittableReadableDb extends DbWithRead {

  final def newTransaction: DbTransaction with DbIO = UnitOfWork(this)

  def commitUnitOfWork(uow: UnitOfWork): DbTransaction.CommitResult

}
