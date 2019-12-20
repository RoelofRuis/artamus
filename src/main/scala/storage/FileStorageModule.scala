package storage

import net.codingwell.scalaguice.ScalaPrivateModule
import storage.api.DbWithRead
import storage.impl.file.FileDb

class FileStorageModule extends ScalaPrivateModule {
  this: FileStorageConfig =>

  override def configure(): Unit = {
    bind[String].annotatedWithName("db-root-path").toInstance(dbRoot)
    bind[Int].annotatedWithName("cleanup-threshold").toInstance(cleanupThreshold)

    bind[DbWithRead].to[FileDb]

    expose[DbWithRead]
  }

}
