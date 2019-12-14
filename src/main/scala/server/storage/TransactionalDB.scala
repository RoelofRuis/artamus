package server.storage

import scala.util.Try

@deprecated
trait TransactionalDB {

  def commit(): Try[Unit]
  def rollback(): Try[Unit]

}
