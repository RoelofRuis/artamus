package server.storage.entity

import music.domain.user.User
import server.storage.api.{DataKey, DbIO, DbRead, ResourceNotFound}
import server.storage.model.DomainProtocol

object Users {

  import server.storage.JsonDB._

  private val KEY = DataKey("user")

  object UserJsonProtocol extends DomainProtocol {
    final case class UserListModel(users: Seq[User] = Seq())

    implicit val userListModel = jsonFormat1(UserListModel)
  }

  import UserJsonProtocol._

  implicit class UserCommands(db: DbIO) {
    // TODO: condense the shit out of this logic!
    def saveUser(user: User): EntityResult[Unit] = {
      def read: EntityResult[UserListModel] = {
        db.read[UserListModel](KEY) match {
          case Left(_: ResourceNotFound) => EntityResult.found(UserListModel())
          case Right(model) => EntityResult.found(model)
          case Left(ex) => EntityResult.badData(ex)
        }
      }

      def update(model: UserListModel): UserListModel = UserListModel(
        model.users :+ user
      )

      def write(model: UserListModel): EntityResult[Unit] = {
        db.write(KEY, model) match {
          case Right(_) => EntityResult.ok
          case Left(ex) => EntityResult.badData(ex)
        }
      }

      for {
        model <- read
        _ <- write(update(model))
      } yield ()
    }
  }

  implicit class UserQueries(db: DbRead) {
    def getUserByName(name: String): EntityResult[User] = {
      db.read[UserListModel](KEY) match {
        case Left(_: ResourceNotFound) => EntityResult.notFound
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
