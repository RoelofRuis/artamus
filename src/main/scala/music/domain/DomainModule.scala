package music.domain

import music.domain.track.TrackRepository
import music.domain.user.UserRepository
import music.domain.workspace.{FileWorkspaceRepository, WorkspaceRepository}
import net.codingwell.scalaguice.ScalaPrivateModule

class DomainModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    bind[UserRepository]
    bind[TrackRepository]
    bind[WorkspaceRepository].to[FileWorkspaceRepository]

    expose[UserRepository]
    expose[TrackRepository]
    expose[WorkspaceRepository]
  }

}
