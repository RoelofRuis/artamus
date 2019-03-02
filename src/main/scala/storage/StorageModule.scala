package storage

import core.Idea
import core.components.Storage
import net.codingwell.scalaguice.ScalaModule

class StorageModule extends ScalaModule {

  override def configure(): Unit = {
    bind[Storage[Idea]].to[InMemoryStore[Idea]]
  }

}
