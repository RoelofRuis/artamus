package server.storage

import net.codingwell.scalaguice.ScalaPrivateModule
import server.storage.api.DbWithRead
import server.storage.impl.InMemoryDb

class InMemoryStorageModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    bind[DbWithRead].toInstance(new InMemoryDb())

    expose[DbWithRead]
  }

}
