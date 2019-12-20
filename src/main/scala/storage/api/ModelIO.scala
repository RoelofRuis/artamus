package storage.api

import spray.json.{JsonReader, JsonWriter}

object ModelIO {

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



  import storage.impl.JsonIO._

  implicit class ModelReadOps(db: DbRead) {
    def readModel[A: JsonReader](key: DataKey, default: Option[A] = None): ModelResult[A] = {
      db.read[A](key) match {
        case Left(_: ResourceNotFound) =>
          default match {
            case Some(default) => ModelResult.found(default)
            case None => ModelResult.notFound
          }
        case Right(model) => ModelResult.found(model)
        case Left(ex) => ModelResult.badData(ex)
      }
    }
  }

  implicit class ModelWriteOps(db: DbIO) {
    def writeModel[A : JsonWriter](key: DataKey, model: A): ModelResult[Unit] = {
      db.write(key, model) match {
        case Right(_) => ModelResult.ok
        case Left(ex) => ModelResult.badData(ex)
      }
    }

    def updateModel[A : JsonReader : JsonWriter](key: DataKey, default: A, f: A => A): ModelResult[Unit] = {
      for {
        model <- db.readModel(key, Some(default))
        _ <- writeModel(key, f(model))
      } yield ()
    }
  }

}
