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
    def failure[A](error: DatabaseError): DbResult[A] = Left(error)
  }

  // TODO: maybe make into explicit class with transformations
  type ModelResult[A] = Either[ModelException, A]

  object ModelResult {
    def badData[A](ex: DatabaseError): ModelResult[A] = Left(BadData(ex))
    def notFound[A]: ModelResult[A] = Left(NotFound())
    def found[A](a: A): ModelResult[A] = Right(a)
    def ok: ModelResult[Unit] = Right(())
  }

  sealed trait ModelException extends Exception
  final case class NotFound() extends ModelException
  final case class BadData(ex: DatabaseError) extends ModelException

}
