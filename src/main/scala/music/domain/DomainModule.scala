package music.domain

import music.domain.track.TrackRepository
import music.domain.user.UserRepository
import music.domain.workspace.WorkspaceRepository
import net.codingwell.scalaguice.ScalaPrivateModule

class DomainModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    bind[UserRepository].asEagerSingleton()
    bind[TrackRepository].asEagerSingleton()
    bind[WorkspaceRepository].asEagerSingleton()

    expose[UserRepository]
    expose[TrackRepository]
  }

}
