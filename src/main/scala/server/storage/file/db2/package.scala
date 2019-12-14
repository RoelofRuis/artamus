package server.storage.file

package object db2 {

  final case class Key(key: String)

  sealed trait DatabaseError extends Exception

  final case class IOError(cause: Throwable) extends DatabaseError
  final case class DataCorruptionException(cause: Throwable) extends DatabaseError
  final case class KeyNotFoundException(key: Key) extends DatabaseError

}
