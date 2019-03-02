package core

import com.google.inject.Key
import core.app.AppRunner
import net.codingwell.scalaguice.ScalaModule

class CoreModule extends ScalaModule {

  override def configure(): Unit = {
    requireBinding(new Key[AppRunner]() {})

    bind[IdeaRepository].asEagerSingleton()
  }

}
