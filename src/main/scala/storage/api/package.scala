package storage

package object api {

  trait DbIO extends DbRead with DbWrite
  trait DbWithRead extends Db with DbRead

  final case class DataKey(name: String)

  type ModelResult[A] = Either[ModelException, A]

  object ModelResult {
    def badData[A](ex: DatabaseError): ModelResult[A] = Left(BadData(ex))
    def notFound[A]: ModelResult[A] = Left(NotFound())
    def found[A](a: A): ModelResult[A] = Right(a)
    def ok: ModelResult[Unit] = Right(())
  }

  // TODO: should be renamed to storage exception
  sealed trait ModelException extends Exception
  final case class NotFound() extends ModelException
  final case class BadData(ex: DatabaseError) extends ModelException


  def recoverNotFound[A](res: ModelResult[A], default: A): ModelResult[A] = {
    res match {
      case Left(_: NotFound) => ModelResult.found(default)
      case x => x
    }
  }


  // TODO: try to remove these!
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
  }

}
