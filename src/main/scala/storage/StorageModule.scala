package storage

import application.model.Idea.Idea_ID
import application.model.Track.TrackType
import application.model._
import application.ports.KeyValueStorage
import net.codingwell.scalaguice.ScalaModule
import storage.memory.InMemoryKeyValueStorage

class StorageModule extends ScalaModule {

  override def configure(): Unit = {
    bind[KeyValueStorage[Idea_ID, Idea]].to[InMemoryKeyValueStorage[Idea_ID, Idea]].asEagerSingleton()
    bind[KeyValueStorage[(Idea_ID, TrackType), Track[Note]]].to[InMemoryKeyValueStorage[(Idea_ID, TrackType), Track[Note]]].asEagerSingleton()
  }

}
