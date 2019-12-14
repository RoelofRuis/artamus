package server.storage.neww

import music.domain.user.User
import server.storage.file.db2.{DbRead, Key}
import server.storage.file.model.DomainProtocol

object Users {

  import server.storage.file.db2.JsonDB._

  private val KEY = Key("user")

  object UserJsonProtocol extends DomainProtocol {
    final case class UserListModel(users: Seq[User] = Seq())

    implicit val userListModel = jsonFormat1(UserListModel)
  }

  import UserJsonProtocol._

  implicit class UserQueries(db: DbRead) {
    def getUserByName(name: String): DomainResult[User] = {
      db.read[UserListModel](KEY) match {
        case Left(ex) => DomainResult.dbError(ex)
        case Right(model) =>
          model.users.find(_.name == name) match {
            case Some(u) => DomainResult.success(u)
            case None => DomainResult.entityNotFound
          }
      }
    }
  }

}
