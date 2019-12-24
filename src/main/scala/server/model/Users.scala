package server.model

import music.model.write.user.User
import spray.json.RootJsonFormat
import storage.api.{DbIO, DbRead, ModelResult}

object Users {

  private implicit val table: JsonTableModel[User] = new JsonTableModel[User] {
    override val tableName: String = "user"
    implicit val format: RootJsonFormat[User] = jsonFormat2(User.apply)
  }

  implicit class UserQueries(db: DbRead) {
    def getUserByName(name: String): ModelResult[User] = {
      storage.api.recoverNotFound(db.readModel[table.Shape], table.empty).flatMap {
        _.find { case (_, user) => (user.name == name) } match {
          case None => ModelResult.notFound
          case Some((_, user)) => ModelResult.found(user)
        }
      }
    }
  }

  implicit class UserCommands(db: DbIO) {
    def saveUser(user: User): ModelResult[Unit] = {
      db.updateModel[table.Shape](
        table.empty,
        _.updated(user.id.toString, user)
      )
    }
  }

}
