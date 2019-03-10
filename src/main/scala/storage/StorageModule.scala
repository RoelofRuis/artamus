package storage

import application.model.Idea
import application.model.Unquantized.UnquantizedTrack
import application.ports.KeyValueStorage
import net.codingwell.scalaguice.ScalaModule
import storage.memory.InMemoryKeyValueStorage

class StorageModule extends ScalaModule {

  override def configure(): Unit = {
    bind[KeyValueStorage[Idea.ID, Idea]].to[InMemoryKeyValueStorage[Idea.ID, Idea]].asEagerSingleton()
    bind[KeyValueStorage[Idea.ID, UnquantizedTrack]].to[InMemoryKeyValueStorage[Idea.ID, UnquantizedTrack]].asEagerSingleton()
  }

}
