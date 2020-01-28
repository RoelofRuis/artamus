package storage

import net.codingwell.scalaguice.ScalaPrivateModule
import storage.api.Database
import storage.impl.memory.InMemoryDatabase

class InMemoryStorageModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    bind[Database].to[InMemoryDatabase]

    expose[Database]
  }

}
