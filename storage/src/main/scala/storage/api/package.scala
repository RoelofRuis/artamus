package storage

package object api {

  trait DbIO extends ModelReader with ModelWriter

  sealed trait DbException extends Exception
  final case class NotFound() extends DbException
  final case class IOError(cause: Throwable) extends DbException

  type DbResult[A] = Either[DbException, A]

  implicit class DbResultOps[A](res: DbResult[A]) {
    def ifNotFound(default: A): DbResult[A] = {
      res match {
        case Left(_: NotFound) => DbResult.found(default)
        case x => x
      }
    }
  }

  object DbResult {
    def ioError[A](ex: Throwable): DbResult[A] = Left(IOError(ex))
    def notFound[A]: DbResult[A] = Left(NotFound())
    def found[A](a: A): DbResult[A] = Right(a)
    def ok: DbResult[Unit] = Right(())
  }

}
