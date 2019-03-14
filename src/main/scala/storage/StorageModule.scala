package storage

import application.model.Track.TrackType
import application.model._
import application.ports.KeyValueStorage
import net.codingwell.scalaguice.ScalaModule
import storage.memory.InMemoryKeyValueStorage

class StorageModule extends ScalaModule {

  override def configure(): Unit = {
    bind[KeyValueStorage[ID[Idea.type], Idea]].to[InMemoryKeyValueStorage[ID[Idea.type], Idea]].asEagerSingleton()
    bind[KeyValueStorage[(ID[Idea.type], TrackType), Track[Note]]].to[InMemoryKeyValueStorage[(ID[Idea.type], TrackType), Track[Note]]].asEagerSingleton()
  }

}
