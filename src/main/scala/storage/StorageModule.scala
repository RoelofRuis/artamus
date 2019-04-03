package storage

import application.api.KeyValueStorage
import application.model.Track
import net.codingwell.scalaguice.ScalaModule
import storage.memory.InMemoryKeyValueStorage

class StorageModule extends ScalaModule {

  override def configure(): Unit = {
    bind[KeyValueStorage[Track.TrackID, Track]].to[InMemoryKeyValueStorage[Track.TrackID, Track]].asEagerSingleton()
  }

}
