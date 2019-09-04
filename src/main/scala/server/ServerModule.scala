package server

import com.google.inject.Provides
import javax.inject.Singleton
import net.codingwell.scalaguice.ScalaPrivateModule
import protocol.ServerInterface.{Dispatcher, EventBus}
import protocol.{Command, Control, Query, ServerInterface}
import server.control.ControlHandler
import server.domain.track.{TrackCommandHandler, TrackQueryHandler, TrackState}

class ServerModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    bind[ServerInterface].toInstance(protocol.createServer(9999))

    bind[Dispatcher[Control]].toInstance(protocol.createDispatcher[Control]())
    bind[ControlHandler].asEagerSingleton()

    bind[Dispatcher[Command]].toInstance(protocol.createDispatcher[Command]())
    bind[TrackCommandHandler].asEagerSingleton()

    bind[Dispatcher[Query]].toInstance(protocol.createDispatcher[Query]())
    bind[TrackQueryHandler].asEagerSingleton()

    bind[TrackState].asEagerSingleton()

    bind[Bootstrapper].asEagerSingleton()
    expose[Bootstrapper]
  }

  @Provides @Singleton
  def eventBus(serverInterface: ServerInterface): EventBus = serverInterface.getEventBus

}
