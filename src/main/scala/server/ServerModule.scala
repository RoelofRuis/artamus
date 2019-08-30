package server

import net.codingwell.scalaguice.ScalaPrivateModule
import protocol.ServerInterface
import server.domain.TrackState
import server.handler._

class ServerModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    bind[ServerInterface].toInstance(protocol.createServer(9999))

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
