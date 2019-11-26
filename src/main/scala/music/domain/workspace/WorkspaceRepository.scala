package music.domain.workspace

import music.domain.user.User

trait WorkspaceRepository {

  def getByOwner(user: User): Workspace

  def put(workspace: Workspace): Unit

}
