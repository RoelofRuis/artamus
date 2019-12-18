package storage.api

import spray.json.{JsonReader, JsonWriter}

object ModelIO {

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
