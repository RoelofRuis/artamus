package server.storage

import net.codingwell.scalaguice.ScalaPrivateModule
import server.storage.api.DbWithRead

class StorageModule extends ScalaPrivateModule {
  this: FileStorageConfig =>

  override def configure(): Unit = {
    bind[DbWithRead].toInstance(new FileDb(dbRoot))

    expose[DbWithRead]
  }

}
