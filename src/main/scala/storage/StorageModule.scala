package storage

import server.api.Actions.TrackID
import server.api.KeyValueStorage
import server.model.Track
import net.codingwell.scalaguice.ScalaModule
import storage.memory.InMemoryKeyValueStorage

class StorageModule extends ScalaModule {

  override def configure(): Unit = {
    bind[KeyValueStorage[TrackID, Track]].to[InMemoryKeyValueStorage[TrackID, Track]].asEagerSingleton()
  }

}
