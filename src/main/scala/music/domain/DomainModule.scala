package music.domain

import music.domain.track.TrackRepository
import music.domain.workspace.WorkspaceRepository
import net.codingwell.scalaguice.ScalaPrivateModule

class DomainModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    requireBinding(classOf[TrackRepository])
    requireBinding(classOf[WorkspaceRepository])
  }

}
