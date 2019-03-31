package storage

import application.api.KeyValueStorage
import application.model.event.Track
import application.model.event.Track.Track_ID
import net.codingwell.scalaguice.ScalaModule
import storage.memory.InMemoryKeyValueStorage

class StorageModule extends ScalaModule {

  override def configure(): Unit = {
    bind[KeyValueStorage[Track_ID, Track]].to[InMemoryKeyValueStorage[Track_ID, Track]].asEagerSingleton()
  }

}
