package storage

package object api {

  trait DbIO extends DbRead with DbWrite
  trait DbWithRead extends Db with DbRead

  final case class DataKey(name: String)

  sealed trait DatabaseError extends Exception

  final case class IOError(cause: Throwable) extends DatabaseError
  final case class DataCorruptionException(cause: Throwable) extends DatabaseError
  final case class ResourceNotFound() extends DatabaseError

  type DbResult[A] = Either[DatabaseError, A]

  object DbResult {
    def success[A](a: A): DbResult[A] = Right(a)
    def done: DbResult[Unit] = Right(())
    def ioError[A](cause: Throwable): DbResult[A] = Left(IOError(cause))
    def notFound[A]: DbResult[A] = Left(ResourceNotFound())
    def corruptData[A](cause: Throwable): DbResult[A] = Left(DataCorruptionException(cause))
  }

}
