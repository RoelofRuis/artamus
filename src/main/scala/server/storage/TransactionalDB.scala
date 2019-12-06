package server.storage

import scala.util.Try

trait TransactionalDB {

  def commit(): Try[Unit]
  def rollback(): Try[Unit]

}
