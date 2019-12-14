package server.storage.file

package object db2 {

  trait DbIO extends DbRead with DbWrite

  final case class Key(name: String)

  sealed trait DatabaseError extends Exception

  final case class IOError(cause: Throwable) extends DatabaseError
  final case class DataCorruptionException(cause: Throwable) extends DatabaseError
  final case class FileNotFound() extends DatabaseError

  type DbResult[A] = Either[DatabaseError, A]

  object DbResult {
    def success[A](a: A): DbResult[A] = Right(a)
    def done: DbResult[Unit] = Right(())
    def failure[A](error: DatabaseError): DbResult[A] = Left(error)
  }

}
