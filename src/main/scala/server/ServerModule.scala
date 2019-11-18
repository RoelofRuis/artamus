package server

import _root_.server.analysis._
import _root_.server.control.ServerControlHandler
import _root_.server.domain.track.{Savepoint, TrackCommandHandler, TrackQueryHandler}
import com.google.inject.Provides
import javax.inject.Singleton
import music.domain.track.Track2
import net.codingwell.scalaguice.ScalaPrivateModule
import protocol._
import pubsub.{Dispatcher, EventBus}
import server.analysis.blackboard.Controller
import server.domain.ChangeHandler
import server.interpret.LilypondInterpreter
import server.rendering.{RenderingCompletionHandler, RenderingModule}

class ServerModule extends ScalaPrivateModule with ServerConfig {

  override def configure(): Unit = {
    bind[LilypondInterpreter].toInstance(
      new LilypondInterpreter(
        lyVersion,
        paperSize
      )
    )
    bind[RenderingCompletionHandler].to[RenderingEventCompletionHandler]
    install(new RenderingModule with ServerConfig)

    bind[ServerControlHandler].asEagerSingleton()
    bind[ChangeHandler].asEagerSingleton()

    bind[Dispatcher[Command]].toInstance(pubsub.createDispatcher[Command]())
    bind[TrackCommandHandler].asEagerSingleton()

    bind[Dispatcher[Query]].toInstance(pubsub.createDispatcher[Query]())
    bind[TrackQueryHandler].asEagerSingleton()

    bind[Savepoint].asEagerSingleton()
    bind[EventBus[Event]].toInstance(new EventBus[Event])

    bind[Controller[Track2]]
      .toInstance(new Controller(
        Seq(
          new ChordAnalyser(),
          new PitchSpellingAnalyser(),
          new PitchHistogramAnalyser()
        ),
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
