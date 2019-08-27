package server

import net.codingwell.scalaguice.ScalaPrivateModule
import server.Logger.CmdLogger
import server.handler._
import server.interact._

class CoreModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    bind[Logger].toInstance(new CmdLogger(true))
    expose[Logger]

    bind[SocketCommandBus].asEagerSingleton()
    expose[SocketCommandBus]
    bind[ApplicationEventBus].asEagerSingleton()

    bind[TrackCommandHandler].asEagerSingleton()
    bind[ApplicationCommandHandler].asEagerSingleton()
  }

}
