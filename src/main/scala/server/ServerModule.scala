package server

import net.codingwell.scalaguice.ScalaPrivateModule
import protocol.Server
import server.handler._

class ServerModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    bind[Server].toInstance(protocol.server(9999))

    bind[ControlHandlerImpl].asEagerSingleton()

    bind[CommandHandlerImpl].asEagerSingleton()
    bind[TrackCommandHandler].asEagerSingleton()

    bind[QueryHandlerImpl].asEagerSingleton()

    bind[Bootstrapper].asEagerSingleton()
    expose[Bootstrapper]
  }

}
