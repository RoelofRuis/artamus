package storage

import application.api.KeyValueStorage
import application.domain.Idea.Idea_ID
import application.domain.Track.Track_ID
import application.domain._
import net.codingwell.scalaguice.ScalaModule
import storage.memory.InMemoryKeyValueStorage

class StorageModule extends ScalaModule {

  override def configure(): Unit = {
    bind[KeyValueStorage[Idea_ID, Idea]].to[InMemoryKeyValueStorage[Idea_ID, Idea]].asEagerSingleton()
    bind[KeyValueStorage[Track_ID, Track]].to[InMemoryKeyValueStorage[Track_ID, Track]].asEagerSingleton()
  }

}
