package server

import java.net.ServerSocket

import _root_.server.control.{EventBusHandler, ServerControlHandler}
import _root_.server.domain.track.{TrackCommandHandler, TrackQueryHandler, TrackState}
import _root_.server.view.{ChordView, LilypondView}
import com.google.inject.Provides
import javax.inject.Singleton
import net.codingwell.scalaguice.ScalaPrivateModule
import protocol._
import protocol.server._
import pubsub.{Dispatcher, EventBus}
import resource.Resource

class ServerModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    bind[ServerInterface].to[SimpleServer]
    bind[Resource[ServerSocket]].toInstance(Sockets.serverOnPort(9999))

    bind[ServerControlHandler].asEagerSingleton()
    bind[EventBusHandler].asEagerSingleton()

    bind[Dispatcher[Command]].toInstance(protocol.createDispatcher[Command]())
    bind[TrackCommandHandler].asEagerSingleton()

    bind[Dispatcher[Query]].toInstance(protocol.createDispatcher[Query]())
    bind[TrackQueryHandler].asEagerSingleton()

    bind[TrackState].asEagerSingleton()

    bind[EventBus[Event]].toInstance(new EventBus[Event])

    bind[LilypondView].asEagerSingleton()
    bind[ChordView].asEagerSingleton()

    bind[Bootstrapper].asEagerSingleton()
    expose[Bootstrapper]
  }

  @Provides @Singleton
  def serverConnectionFactory(
    commandDispatcher: Dispatcher[Command],
    queryDispatcher: Dispatcher[Query],
    eventBus: EventBus[Event],
  ): ServerConnectionFactory = {
    new ServerConnectionFactory(
      ServerBindings(
        commandDispatcher,
        queryDispatcher,
        eventBus
      )
    )
  }

}
