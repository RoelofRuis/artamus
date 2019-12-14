package server.storage.api

import server.storage.api.DbTransaction.CommitResult

trait DbTransaction {

  def commit(): CommitResult

}

object DbTransaction {

  type CommitResult = Either[CommitError, Int]

  final case class CommitError(causes: Seq[DatabaseError]) extends Exception

  object CommitResult {
    def nothingToCommit: CommitResult = Right(0)
    def success(writes: Int) = Right(writes)
    def failure(causes: DatabaseError*) = Left(CommitError(causes))
  }

}