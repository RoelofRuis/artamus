package music.domain.workspace

import music.domain.user.User

import scala.util.Try

@deprecated
trait WorkspaceRepository {

  def getByOwner(user: User): Try[Workspace]

  def put(workspace: Workspace): Try[Unit]

}
