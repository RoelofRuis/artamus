package storage.api

import storage.api.Transaction.CommitResult

trait Transaction {

  def commit(): CommitResult

}

object Transaction {

  type CommitResult = Either[CommitError, Int]

  final case class CommitError(causes: Seq[DbException]) extends Exception

  object CommitResult {
    def nothingToCommit: CommitResult = Right(0)
    def success(writes: Int) = Right(writes)
    def failure(causes: DbException*) = Left(CommitError(causes))
  }

}