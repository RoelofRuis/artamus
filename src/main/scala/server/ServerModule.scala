package server

import net.codingwell.scalaguice.ScalaPrivateModule
import protocol.Server
import server.domain.TrackState
import server.handler._

class ServerModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    bind[Server].toInstance(protocol.server(9999))

    bind[ControlDispatcherImpl].asEagerSingleton()

    bind[CommandDispatcherImpl].asEagerSingleton()
    bind[TrackCommandHandler].asEagerSingleton()

    bind[QueryDispatcherImpl].asEagerSingleton()
    bind[TrackQueryHandler].asEagerSingleton()

    bind[TrackState].asEagerSingleton()

    bind[Bootstrapper].asEagerSingleton()
    expose[Bootstrapper]
  }

}
