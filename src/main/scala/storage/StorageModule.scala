package storage

import application.model.{Idea, Note, Track}
import application.ports.KeyValueStorage
import net.codingwell.scalaguice.ScalaModule
import storage.memory.InMemoryKeyValueStorage

class StorageModule extends ScalaModule {

  override def configure(): Unit = {
    bind[KeyValueStorage[Idea.ID, Idea]].to[InMemoryKeyValueStorage[Idea.ID, Idea]].asEagerSingleton()
    bind[KeyValueStorage[Idea.ID, Track[Note]]].to[InMemoryKeyValueStorage[Idea.ID, Track[Note]]].asEagerSingleton()
  }

}
