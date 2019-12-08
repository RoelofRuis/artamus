package server

package object storage {

  sealed trait DatabaseException extends Throwable
  final case class EntityNotFoundException(name: String) extends DatabaseException
  final case class DBIOException(cause: Throwable) extends DatabaseException

}
