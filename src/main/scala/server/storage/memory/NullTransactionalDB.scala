package server.storage.memory

import javax.inject.Singleton
import server.storage.TransactionalDB

import scala.util.{Success, Try}

@deprecated
@Singleton
class NullTransactionalDB extends TransactionalDB {
  override def commit(): Try[Unit] = Success(())
  override def rollback(): Try[Unit] = Success(())
}
