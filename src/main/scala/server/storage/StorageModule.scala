package server.storage

import net.codingwell.scalaguice.ScalaPrivateModule

class StorageModule extends ScalaPrivateModule {
  this: FileStorageConfig =>

  override def configure(): Unit = {
    bind[FileDb].toInstance(new FileDb(dbRoot))

    expose[FileDb]
  }

}
