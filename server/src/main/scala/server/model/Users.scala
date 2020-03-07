package server.model

import domain.workspace.User
import domain.workspace.User.UserId
import spray.json.RootJsonFormat
import storage.api.DataTypes.JSON
import storage.api.{DataTypes, DbResult, DbIO, DbReader}

object Users {

  private implicit val table: JsonDataModel[User, UserId] = new JsonDataModel[User, UserId] {
    override implicit val format: RootJsonFormat[User] = jsonFormat2(User.apply)
    override val name: String = "user"
    override val dataType: DataTypes.DataType = JSON
    override def objectId(obj: User): UserId = obj.id
    override def serializeId(id: UserId): String = id.id.toString
  }

  implicit class UserQueries(db: DbReader) {
    def getUserByName(name: String): DbResult[User] = {
      db.readTable.flatMap {
        _.find(_.name == name) match {
          case None => DbResult.notFound
          case Some(user) => DbResult.found(user)
        }
      }
    }
  }

  implicit class UserCommands(db: DbIO) {
    def saveUser(user: User): DbResult[Unit] = db.writeRow(user)
  }

}
