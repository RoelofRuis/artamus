package server.storage.file

import com.typesafe.scalalogging.LazyLogging
import javax.inject.{Inject, Singleton}
import music.domain.user.{User, UserRepository}
import server.storage.file.db.{FileDB, JsonIO, Query}
import server.storage.file.model.DomainProtocol

import scala.util.Try

@Singleton
class FileUserRepository @Inject() (
  db: FileDB,
) extends UserRepository with LazyLogging {

  import JsonIO._

  private val ID = "user"

  object UserJsonProtocol extends DomainProtocol {
    final case class UserListModel(users: Seq[User] = Seq())

    implicit val userListModel = jsonFormat1(UserListModel)
  }

  import UserJsonProtocol._

  def getByName(name: String): Try[User] = {
    db.readByQuery[UserListModel, User](Query(ID, _.users.find(_.name == name)))
  }
}
