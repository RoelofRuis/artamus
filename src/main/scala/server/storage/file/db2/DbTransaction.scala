package server.storage.file.db2

import server.storage.file.db2.DbTransaction.{CommitResult, RollbackResult}

trait DbTransaction {

  def commit(): CommitResult

  def rollback(): RollbackResult

}

object DbTransaction {

  type CommitResult = Either[DatabaseError, Unit]
  type RollbackResult = Either[DatabaseError, Unit]

}