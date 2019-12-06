package server.storage.file

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import javax.inject.{Inject, Singleton}
import music.domain.user.{User, UserRepository}
import server.storage.EntityNotFoundException
import server.storage.file.model.DomainProtocol
import server.storage.io.JsonStorage

import scala.util.{Failure, Success, Try}

@Singleton
class FileUserRepository @Inject() (
  storage: JsonStorage,
) extends UserRepository with LazyLogging {

  private val PATH = new File("data/store/users.json")

  final case class UserListModel(users: Seq[User] = Seq())

  object UserJsonProtocol extends DomainProtocol {
    implicit val userListModel = jsonFormat1(UserListModel)
  }

  import UserJsonProtocol._

  def getByName(name: String): Try[User] = {
    storage.read[UserListModel](PATH, UserListModel()) match {
      case Failure(ex) => Failure(ex)
      case Success(storage) =>
        storage.users.find(_.name == name) match {
          case Some(user) => Success(user)
          case None => Failure(EntityNotFoundException("User"))
        }
    }
  }
}
