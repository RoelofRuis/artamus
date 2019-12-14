package server.storage.file

import music.domain.track.TrackRepository
import music.domain.workspace.WorkspaceRepository
import net.codingwell.scalaguice.ScalaPrivateModule
import server.storage.file.db.FileDB
import server.storage.file.db2.FileDb2

class FileStorageModule extends ScalaPrivateModule {
  this: FileStorageConfig =>

  override def configure(): Unit = {
    bind[FileDb2].toInstance(new FileDb2(dbRoot))

    bind[FileDB].toInstance(new FileDB(dbRoot))
    bind[Boolean].annotatedWithName("compact-json").toInstance(compactJson)

    bind[WorkspaceRepository].to[FileWorkspaceRepository]
    bind[TrackRepository].to[FileTrackRepository]

    expose[FileDb2]
    expose[FileDB]
    expose[WorkspaceRepository]
    expose[TrackRepository]
  }

}
