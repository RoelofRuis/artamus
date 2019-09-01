package server

import com.google.inject.Provides
import javax.inject.Singleton
import net.codingwell.scalaguice.ScalaPrivateModule
import protocol.ServerInterface
import protocol.ServerInterface.EventBus
import server.dispatchers.{CommandDispatcherImpl, ControlDispatcherImpl, QueryDispatcherImpl}
import server.domain.track.{TrackCommandHandler, TrackQueryHandler, TrackState}

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

  @Provides @Singleton
  def eventBus(serverInterface: ServerInterface): EventBus = serverInterface.getEventBus

}
