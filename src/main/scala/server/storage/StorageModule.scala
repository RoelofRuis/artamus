package server.storage

import music.domain.track.TrackRepository
import music.domain.user.UserRepository
import music.domain.workspace.WorkspaceRepository
import net.codingwell.scalaguice.ScalaPrivateModule
import server.storage.file.db.{FileDB, JsonFileDB, JsonMarshaller}
import server.storage.file.{FileTrackRepository, FileUserRepository, FileWorkspaceRepository}

class StorageModule extends ScalaPrivateModule {
  this: StorageConfig =>

  override def configure(): Unit = {
    bind[JsonMarshaller].toInstance(new JsonMarshaller(compactJson))
    bind[FileDB].toInstance(new FileDB(Seq("data", "store")))

    bind[JsonFileDB]

    bind[WorkspaceRepository].to[FileWorkspaceRepository]
    bind[UserRepository].to[FileUserRepository]
    bind[TrackRepository].to[FileTrackRepository]

    expose[WorkspaceRepository]
    expose[UserRepository]
    expose[TrackRepository]
  }

}
