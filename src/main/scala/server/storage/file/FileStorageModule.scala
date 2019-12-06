package server.storage.file

import music.domain.track.TrackRepository
import music.domain.user.UserRepository
import music.domain.workspace.WorkspaceRepository
import net.codingwell.scalaguice.ScalaPrivateModule
import server.storage.file.db.{FileDB, JsonFileDB, JsonMarshaller}

class FileStorageModule extends ScalaPrivateModule {
  this: FileStorageConfig =>

  override def configure(): Unit = {
    bind[JsonMarshaller].toInstance(new JsonMarshaller(compactJson))
    bind[FileDB].toInstance(new FileDB(dbRoot))

    bind[JsonFileDB]

    bind[WorkspaceRepository].to[FileWorkspaceRepository]
    bind[UserRepository].to[FileUserRepository]
    bind[TrackRepository].to[FileTrackRepository]

    expose[FileDB]
    expose[WorkspaceRepository]
    expose[UserRepository]
    expose[TrackRepository]
  }

}
