package storage

import core.Idea
import core.components.Storage
import net.codingwell.scalaguice.ScalaModule
import storage.memory.InMemoryStorage

class StorageModule extends ScalaModule {


  override def configure(): Unit = {
//    import storage.file.Serializers._
//    bind[Storage[Idea]].toInstance(new SimpleFileStorage[Idea]("idea"))

    bind[Storage[Idea]].toInstance(new InMemoryStorage[Idea])
  }

}
