package storage.impl

import storage.api.{Database, DbIO, Transaction}

private[impl] trait TransactionalDatabase extends Database {

  final def newTransaction: Transaction with DbIO = UnitOfWork(this)

  def commitUnitOfWork(uow: UnitOfWork): Transaction.CommitResult

}
