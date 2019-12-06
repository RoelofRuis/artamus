package server.storage.memory

import music.domain.track.TrackRepository
import music.domain.user.UserRepository
import music.domain.workspace.WorkspaceRepository
import net.codingwell.scalaguice.ScalaPrivateModule

class InMemoryStorageModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    bind[WorkspaceRepository].to[InMemoryWorkspaceRepository]
    bind[UserRepository].to[InMemoryUserRepository]
    bind[TrackRepository].to[InMemoryTrackRepository]

    expose[WorkspaceRepository]
    expose[UserRepository]
    expose[TrackRepository]
  }

}
