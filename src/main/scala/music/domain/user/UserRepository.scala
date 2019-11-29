package music.domain.user

import scala.util.Try

trait UserRepository {

  def getByName(name: String): Try[User]

}
