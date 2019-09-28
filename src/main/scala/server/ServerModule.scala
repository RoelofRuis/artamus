package server

import _root_.server.control.{EventBusHandler, ServerControlHandler}
import _root_.server.domain.track.{TrackCommandHandler, TrackQueryHandler, TrackState}
import _root_.server.rendering.LilypondRenderingService
import _root_.server.view.{ChordView, LilypondView}
import com.google.inject.Provides
import javax.inject.Singleton
import net.codingwell.scalaguice.ScalaPrivateModule
import protocol._
import pubsub.{BufferedEventBus, Dispatcher}

class ServerModule extends ScalaPrivateModule with ServerConfig {

  override def configure(): Unit = {
    bind[ServerControlHandler].asEagerSingleton()
    bind[EventBusHandler].asEagerSingleton()

    bind[Dispatcher[Command]].toInstance(pubsub.createDispatcher[Command]())
    bind[TrackCommandHandler].asEagerSingleton()

    bind[Dispatcher[Query]].toInstance(pubsub.createDispatcher[Query]())
    bind[TrackQueryHandler].asEagerSingleton()

    bind[TrackState].asEagerSingleton()

    bind[BufferedEventBus[Event]].toInstance(new BufferedEventBus[Event])

    bind[LilypondRenderingService].toInstance(new LilypondRenderingService(resourceRootPath, cleanupLySources))
    bind[LilypondView].asEagerSingleton()
    bind[ChordView].asEagerSingleton()

    bind[Bootstrapper].asEagerSingleton()
    expose[Bootstrapper]
  }

  @Provides @Singleton
  def serverConnectionFactory(
    commandDispatcher: Dispatcher[Command],
    queryDispatcher: Dispatcher[Query],
    eventBus: BufferedEventBus[Event],
  ): ServerInterface = {
    DefaultServer.apply(
      port,
      ProtocolServerBindings(
        commandDispatcher,
        queryDispatcher,
        eventBus
      )
    )
  }

}
