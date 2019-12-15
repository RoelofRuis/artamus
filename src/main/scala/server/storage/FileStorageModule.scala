package server.storage

import net.codingwell.scalaguice.ScalaPrivateModule
import server.storage.api.DbWithRead
import server.storage.impl.FileDb

class FileStorageModule extends ScalaPrivateModule {
  this: FileStorageConfig =>

  override def configure(): Unit = {
    bind[DbWithRead].toInstance(new FileDb(dbRoot))

    expose[DbWithRead]
  }

}
