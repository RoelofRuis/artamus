package storage

import net.codingwell.scalaguice.ScalaPrivateModule
import storage.api.DbWithRead
import storage.impl.memory.InMemoryDb

class InMemoryStorageModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    bind[DbWithRead].toInstance(new InMemoryDb())

    expose[DbWithRead]
  }

}
