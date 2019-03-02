package storage

import core.Idea
import core.components.Storage
import net.codingwell.scalaguice.ScalaModule
import storage.file.SimpleFileStorage

class StorageModule extends ScalaModule {

  import storage.file.Serializers._

  override def configure(): Unit = {
    bind[Storage[Idea]].toInstance(new SimpleFileStorage[Idea]("idea"))
  }

}
