package server.model

import music.model.write.user.User
import storage.api.{DataKey, DbIO, DbRead, ModelResult}

object Users {

  import storage.api.ModelIO._

  private val KEY = DataKey("user")

  object UserJsonProtocol extends DomainProtocol {
    final case class UserTable(users: Map[String, User] = Map())

    implicit val userFormat = jsonFormat2(User.apply)
    implicit val userTableFormat = jsonFormat1(UserTable)
  }

  import UserJsonProtocol._

  implicit class UserQueries(db: DbRead) {
    def getUserByName(name: String): ModelResult[User] = {
      db.readModel[UserTable](KEY).flatMap {
        _.users.find { case (_, user) => (user.name == name) } match {
          case None => ModelResult.notFound
          case Some((_, user)) => ModelResult.found(user)
        }
      }
    }
  }

  implicit class UserCommands(db: DbIO) {
    def saveUser(user: User): ModelResult[Unit] = {
      db.updateModel[UserTable](
        KEY,
        UserTable(),
        model => UserTable(model.users.updated(user.id.toString, user))
      )
    }
  }

}
