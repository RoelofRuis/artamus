package server.storage

import music.domain.write.user.User
import server.entity.EntityResult
import server.storage.api.{DataKey, DbIO}
import storage.api.DbRead

object Users {

  import storage.EntityIO._

  private val KEY = DataKey("user")

  object UserJsonProtocol extends DomainProtocol {
    final case class UserListModel(users: Seq[User] = Seq())

    implicit val userListModel = jsonFormat1(UserListModel)
  }

  import UserJsonProtocol._

  implicit class UserCommands(db: DbIO) {
    def saveUser(user: User): EntityResult[Unit] = {
      db.updateModel[UserListModel](
        KEY,
        UserListModel(),
        model => UserListModel(model.users :+ user)
      )
    }
  }

  implicit class UserQueries(db: DbRead) {
    def getUserByName(name: String): EntityResult[User] = {
      db.readModel[UserListModel](KEY).flatMap {
        _.users.find(_.name == name) match {
          case None => EntityResult.notFound
          case Some(u) => EntityResult.found(u)
        }
      }
    }
  }

}
