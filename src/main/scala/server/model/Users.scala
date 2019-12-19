package server.model

import music.model.write.user.User
import storage.api.{DataKey, DbIO, DbRead}

object Users {

  import storage.api.ModelIO._

  private val KEY = DataKey("user")

  object UserJsonProtocol extends DomainProtocol {
    final case class UserListModel(users: Seq[User] = Seq())

    implicit val userListModel = jsonFormat1(UserListModel)
  }

  import UserJsonProtocol._

  implicit class UserCommands(db: DbIO) {
    def saveUser(user: User): ModelResult[Unit] = {
      db.updateModel[UserListModel](
        KEY,
        UserListModel(),
        model =>
          if (model.users.contains(user)) model
          else UserListModel(model.users :+ user)
      )
    }
  }

  implicit class UserQueries(db: DbRead) {
    def getUserByName(name: String): ModelResult[User] = {
      db.readModel[UserListModel](KEY).flatMap {
        _.users.find(_.name == name) match {
          case None => ModelResult.notFound
          case Some(u) => ModelResult.found(u)
        }
      }
    }
  }

}
