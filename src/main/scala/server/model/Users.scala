package server.model

import music.model.write.user.User
import spray.json.RootJsonFormat
import storage.api.{DbIO, DbRead}

object Users {

  import storage.api.ModelIO._

  private implicit val table: JsonTableModel[User] = new JsonTableModel[User] {
    override val tableName: String = "user"
    implicit val format: RootJsonFormat[User] = jsonFormat2(User.apply)
  }

  implicit class UserQueries(db: DbRead) {
    def getUserByName(name: String): ModelResult[User] = {
      db.readModel[table.Shape](Some(Map())).flatMap {
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
        Map[String, User](),
        _.updated(user.id.toString, user)
      )
    }
  }

}
