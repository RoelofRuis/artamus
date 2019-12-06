package server.storage

import music.domain.track.TrackRepository
import music.domain.user.UserRepository
import music.domain.workspace.WorkspaceRepository
import net.codingwell.scalaguice.ScalaPrivateModule
import server.storage.file.{FileTrackRepository, FileUserRepository, FileWorkspaceRepository}
import server.storage.io.{BasicJsonStorage, FileIO, JsonStorage}

class StorageModule extends ScalaPrivateModule {
  this: StorageConfig =>

  override def configure(): Unit = {
    bind[JsonStorage].toInstance(
      new BasicJsonStorage(
        new FileIO,
        compactJson
      )
    )
    bind[WorkspaceRepository].to[FileWorkspaceRepository]
    bind[UserRepository].to[FileUserRepository]
    bind[TrackRepository].to[FileTrackRepository]

    expose[WorkspaceRepository]
    expose[UserRepository]
    expose[TrackRepository]
  }

}
