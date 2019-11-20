package music.domain.user

import javax.inject.Inject
import music.domain.user.User.UserId
import music.domain.user.UserRepository.UserImpl
import music.domain.workspace.{Workspace, WorkspaceRepository}

class UserRepository @Inject() (workspaces: WorkspaceRepository) {

  private val users: Array[User] = Array[User](UserImpl(workspaces, UserId(0), "artamus"))

  def getByName(name: String): Option[User] = users.find(_.name == name)

}

object UserRepository {

  final case class UserImpl(
    workspaceRepository: WorkspaceRepository,
    id: UserId,
    name: String,
  ) extends User {
    def workspace: Workspace = workspaceRepository.getByOwner(id)
  }

}