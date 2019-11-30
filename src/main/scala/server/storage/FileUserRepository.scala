package server.storage

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import javax.inject.{Inject, Singleton}
import music.domain.user.User.UserId
import music.domain.user.{User, UserRepository}
import server.storage.io.JsonStorage
import spray.json.DefaultJsonProtocol

import scala.util.{Failure, Success, Try}

@Singleton
class FileUserRepository @Inject() (
  storage: JsonStorage,
) extends UserRepository with LazyLogging {

  private val PATH = new File("data/store/users.json")

  final case class UserListModel(users: Seq[User] = Seq())

  object UserJsonProtocol extends DefaultJsonProtocol {
    implicit val userId = jsonFormat1(UserId)
    implicit val user = jsonFormat2(User.apply)
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
