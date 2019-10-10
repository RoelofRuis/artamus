package server

import _root_.server.analysis._
import _root_.server.control.{ChangeHandler, ServerControlHandler}
import _root_.server.domain.track.{TrackCommandHandler, TrackQueryHandler, TrackState}
import blackboard.Controller
import com.google.inject.Provides
import javax.inject.Singleton
import music.collection.Track
import net.codingwell.scalaguice.ScalaPrivateModule
import protocol._
import pubsub.{Dispatcher, EventBus}
import server.rendering.RenderingModule

class ServerModule extends ScalaPrivateModule with ServerConfig {

  override def configure(): Unit = {
    install(new RenderingModule with ServerConfig)

    bind[ServerControlHandler].asEagerSingleton()
    bind[ChangeHandler].asEagerSingleton()

    bind[Dispatcher[Command]].toInstance(pubsub.createDispatcher[Command]())
    bind[TrackCommandHandler].asEagerSingleton()

    bind[Dispatcher[Query]].toInstance(pubsub.createDispatcher[Query]())
    bind[TrackQueryHandler].asEagerSingleton()

    bind[TrackState].asEagerSingleton()
    bind[EventBus[Event]].toInstance(new EventBus[Event])

    bind[Controller[Track]]
      .toInstance(new Controller(
        Seq(new ChordAnalyser(), new PitchHistogramAnalyser()),
      ))

    bind[Bootstrapper].asEagerSingleton()
    expose[Bootstrapper]
  }

  @Provides @Singleton
  def serverConnectionFactory(
    commandDispatcher: Dispatcher[Command],
    queryDispatcher: Dispatcher[Query],
    eventBus: EventBus[Event],
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
