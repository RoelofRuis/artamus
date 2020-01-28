package storage

import net.codingwell.scalaguice.ScalaPrivateModule
import storage.api.Database
import storage.impl.file.FileDatabase

class FileStorageModule extends ScalaPrivateModule {
  this: FileStorageConfig =>

  override def configure(): Unit = {
    bind[String].annotatedWithName("db-root-path").toInstance(dbRoot)
    bind[Int].annotatedWithName("cleanup-threshold").toInstance(cleanupThreshold)

    bind[Database].to[FileDatabase]

    expose[Database]
  }

}
