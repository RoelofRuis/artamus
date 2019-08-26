package interaction.socket

import application.api.Driver
import net.codingwell.scalaguice.{ScalaMapBinder, ScalaModule}

class SocketModule extends ScalaModule {

  override def configure(): Unit = {
    ScalaMapBinder.newMapBinder[String, Driver](binder)
      .addBinding("socket").to[SocketDriver]
  }

}
