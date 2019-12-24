package storage.api

import scala.util.{Failure, Success}

object ModelIO {

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

  implicit class ModelReadOps(db: DbRead) {
    def readModel[A](default: Option[A] = None)(implicit model: Model[A]): ModelResult[A] = {
      db.readKey(model.key).map(model.deserialize) match {
        case Left(_: ResourceNotFound) =>
          default match {
            case Some(default) => ModelResult.found(default)
            case None => ModelResult.notFound
          }
        case Right(Success(obj)) => ModelResult.found(obj)
        case Right(Failure(ex)) => ModelResult.badData(DataCorruptionException(ex))
        case Left(ex) => ModelResult.badData(ex)
      }
    }
  }

  implicit class ModelWriteOps(db: DbIO) {
    def writeModel[A](obj: A)(implicit model: Model[A]): ModelResult[Unit] = {
      model.serialize(obj) match {
        case Failure(ex) => ModelResult.badData(DataCorruptionException(ex))
        case Success(data) => db.writeKey(model.key, data) match {
          case Right(_) => ModelResult.ok
          case Left(ex) => ModelResult.badData(ex)
        }
      }
    }

    def updateModel[A : Model](default: A, f: A => A): ModelResult[Unit] = {
      for {
        data <- db.readModel(Some(default))
        _ <- writeModel(f(data))
      } yield ()
    }
  }

}
