package server.storage.neww

import music.domain.user.User
import server.storage.file.db2.{DbRead, DataKey}
import server.storage.file.model.DomainProtocol

object Users {

  import server.storage.file.db2.JsonDB._

  private val KEY = DataKey("user")

  object UserJsonProtocol extends DomainProtocol {
    final case class UserListModel(users: Seq[User] = Seq())

    implicit val userListModel = jsonFormat1(UserListModel)
  }

  import UserJsonProtocol._

  implicit class UserQueries(db: DbRead) {
    def getUserByName(name: String): EntityResult[User] = {
      db.read[UserListModel](KEY) match {
        case Left(ex) => EntityResult.badData(ex)
        case Right(model) =>
          model.users.find(_.name == name) match {
            case None => EntityResult.notFound
            case Some(u) => EntityResult.found(u)
          }
      }
    }
  }

}
