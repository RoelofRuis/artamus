package music.domain.workspace

import music.domain.user.User.UserId

class WorkspaceRepository {

  def getByOwner(userId: UserId): Workspace = {
    ??? // TODO: implement!
  }

}
