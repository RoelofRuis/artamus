package core

import com.google.inject.Key
import core.components.{AppRunner, Storage}
import net.codingwell.scalaguice.ScalaModule

class CoreModule extends ScalaModule {

  override def configure(): Unit = {
    requireBinding(new Key[AppRunner]() {})
    requireBinding(new Key[Storage[Idea]]() {})

    bind[IdeaRepository].asEagerSingleton()
  }

}
