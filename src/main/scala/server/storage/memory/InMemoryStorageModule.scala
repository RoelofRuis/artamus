package server.storage.memory

import music.domain.track.TrackRepository
import music.domain.workspace.WorkspaceRepository
import net.codingwell.scalaguice.ScalaPrivateModule
import server.storage.TransactionalDB

@deprecated
class InMemoryStorageModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    bind[WorkspaceRepository].to[InMemoryWorkspaceRepository]
    bind[TrackRepository].to[InMemoryTrackRepository]
    bind[NullTransactionalDB]

    expose[TransactionalDB]
    expose[WorkspaceRepository]
    expose[TrackRepository]
  }

}
