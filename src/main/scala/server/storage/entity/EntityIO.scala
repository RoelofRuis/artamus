package server.storage.entity

import server.storage.api.{DataKey, DbIO, DbRead, ResourceNotFound}
import spray.json.{JsonReader, JsonWriter}

object EntityIO {

  import server.storage.JsonDB._

  implicit class ModelOps(db: DbRead) {
    def readModel[A: JsonReader](key: DataKey, default: Option[A] = None): EntityResult[A] = {
      db.read[A](key) match {
        case Left(_: ResourceNotFound) =>
          default match {
            case Some(default) => EntityResult.found(default)
            case None => EntityResult.notFound
          }
        case Right(model) => EntityResult.found(model)
        case Left(ex) => EntityResult.badData(ex)
      }
    }
  }

  implicit class WriteOps(db: DbIO) {
    def writeModel[A : JsonWriter](key: DataKey, model: A): EntityResult[Unit] = {
      db.write(key, model) match {
        case Right(_) => EntityResult.ok
        case Left(ex) => EntityResult.badData(ex)
      }
    }

    def updateModel[A : JsonReader : JsonWriter](key: DataKey, default: A, f: A => A): EntityResult[Unit] = {
      for {
        model <- db.readModel(key, Some(default))
        _ <- writeModel(key, f(model))
      } yield ()
    }
  }

}
