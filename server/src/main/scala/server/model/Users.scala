package server.model

import music.model.workspace.User
import spray.json.RootJsonFormat
import storage.api.{DbIO, DbResult, ModelReader}

object Users {

  private implicit val table: JsonTableDataModel[User] = new JsonTableDataModel[User] {
    override val tableName: String = "user"
    implicit val format: RootJsonFormat[User] = jsonFormat2(User.apply)
  }

  implicit class UserQueries(db: ModelReader) {
    def getUserByName(name: String): DbResult[User] = {
      db.readModel[table.Shape].ifNotFound(table.empty).flatMap {
        _.find { case (_, user) => (user.name == name) } match {
          case None => DbResult.notFound
          case Some((_, user)) => DbResult.found(user)
        }
      }
    }
  }

  implicit class UserCommands(db: DbIO) {
    def saveUser(user: User): DbResult[Unit] = {
      db.updateModel[table.Shape](
        table.empty,
        _.updated(user.id.toString, user)
      )
    }
  }

}
