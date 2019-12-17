package storage.impl

import storage.api.{DbIO, DbTransaction, DbWithRead}

private[impl] trait CommittableReadableDb extends DbWithRead {

  final def newTransaction: DbTransaction with DbIO = UnitOfWork(this)

  def commitUnitOfWork(uow: UnitOfWork): DbTransaction.CommitResult

}
