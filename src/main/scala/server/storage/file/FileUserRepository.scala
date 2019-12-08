package server.storage.file

import com.typesafe.scalalogging.LazyLogging
import javax.inject.{Inject, Singleton}
import music.domain.user.{User, UserRepository}
import server.storage.EntityNotFoundException
import server.storage.file.db.JsonFileDB
import server.storage.file.model.DomainProtocol

import scala.util.{Failure, Success, Try}

@Singleton
class FileUserRepository @Inject() (
  db: JsonFileDB,
) extends UserRepository with LazyLogging {

  private val ID = "user"

  object UserJsonProtocol extends DomainProtocol {
    final case class UserListModel(users: Seq[User] = Seq())

    implicit val userListModel = jsonFormat1(UserListModel)
  }

  import UserJsonProtocol._

  def getByName(name: String): Try[User] = {
    db.read[UserListModel](ID, UserListModel()) match {
      case Failure(ex) => Failure(ex)
      case Success(storage) =>
        storage.users.find(_.name == name) match {
          case Some(user) => Success(user)
          case None => Failure(EntityNotFoundException("User"))
        }
    }
  }
}
