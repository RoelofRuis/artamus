package storage

import application.api.KeyValueStorage
import application.model.event.MidiTrack
import application.model.event.MidiTrack.Track_ID
import application.model.symbolic.Track
import net.codingwell.scalaguice.ScalaModule
import storage.memory.InMemoryKeyValueStorage

class StorageModule extends ScalaModule {

  override def configure(): Unit = {
    bind[KeyValueStorage[Track.TrackID, Track]].to[InMemoryKeyValueStorage[Track.TrackID, Track]].asEagerSingleton()
    bind[KeyValueStorage[Track_ID, MidiTrack]].to[InMemoryKeyValueStorage[Track_ID, MidiTrack]].asEagerSingleton()
  }

}
