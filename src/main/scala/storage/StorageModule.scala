package storage

import core.components.{KeyValueStorage, Storage}
import core.idea.Idea
import core.musicdata.Part
import net.codingwell.scalaguice.ScalaModule
import storage.memory.{InMemoryKeyValueStorage, InMemoryStorage}

class StorageModule extends ScalaModule {

  override def configure(): Unit = {
//    import storage.file.Serializers._
//    bind[Storage[Idea]].toInstance(new SimpleFileStorage[Idea]("idea"))

    bind[Storage[Idea]].to[InMemoryStorage[Idea]].asEagerSingleton()
    bind[KeyValueStorage[Idea.ID, Part]].to[InMemoryKeyValueStorage[Idea.ID, Part]].asEagerSingleton()
  }

}
