package server

import _root_.server.control.ControlHandler
import _root_.server.domain.track.{TrackCommandHandler, TrackQueryHandler, TrackState}
import com.google.inject.Provides
import javax.inject.Singleton
import net.codingwell.scalaguice.ScalaPrivateModule
import protocol._
import protocol.server.{EventBus, ServerInterface}

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
