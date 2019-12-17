package storage

import net.codingwell.scalaguice.ScalaPrivateModule
import storage.api.DbWithRead
import storage.impl.FileDb

class FileStorageModule extends ScalaPrivateModule {
  this: FileStorageConfig =>

  override def configure(): Unit = {
    bind[DbWithRead].toInstance(new FileDb(dbRoot))

    expose[DbWithRead]
  }

}