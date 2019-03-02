package storage

import core.components.Storage
import core.idea.Idea
import core.musicdata.MusicData
import net.codingwell.scalaguice.ScalaModule
import storage.memory.InMemoryStorage

class StorageModule extends ScalaModule {


  override def configure(): Unit = {
//    import storage.file.Serializers._
//    bind[Storage[Idea]].toInstance(new SimpleFileStorage[Idea]("idea"))

    bind[Storage[Idea]].to[InMemoryStorage[Idea]].asEagerSingleton()
    bind[Storage[MusicData]].to[InMemoryStorage[MusicData]].asEagerSingleton()
  }

}
