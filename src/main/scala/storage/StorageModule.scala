package storage

import application.components.storage.{KeyValueStorage, Storage}
import application.idea.Idea
import application.symbolic.Music.Grid
import net.codingwell.scalaguice.ScalaModule
import storage.memory.{InMemoryKeyValueStorage, InMemoryStorage}

class StorageModule extends ScalaModule {

  override def configure(): Unit = {
//    import storage.file.Serializers._
//    bind[Storage[Idea]].toInstance(new SimpleFileStorage[Idea]("idea"))

    bind[Storage[Idea]].to[InMemoryStorage[Idea]].asEagerSingleton()
    bind[KeyValueStorage[Idea.ID, Grid]].to[InMemoryKeyValueStorage[Idea.ID, Grid]].asEagerSingleton()
  }

}
