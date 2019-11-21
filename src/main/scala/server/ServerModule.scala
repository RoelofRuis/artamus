package server

import _root_.server.analysis._
import _root_.server.control.{DispatchingServerAPI, ServerControlHandler}
import _root_.server.domain.track.{Savepoint, TrackCommandHandler, TrackQueryHandler}
import com.google.inject.Provides
import javax.inject.Singleton
import music.domain.DomainModule
import music.domain.track.Track
import music.domain.user.UserRepository
import net.codingwell.scalaguice.ScalaPrivateModule
import protocol._
import pubsub.{Dispatcher, EventBus}
import server.analysis.blackboard.Controller
import server.domain.ChangeHandler
import server.interpret.LilypondInterpreter
import server.rendering.{RenderingCompletionHandler, RenderingModule}

class ServerModule extends ScalaPrivateModule with ServerConfig {

  override def configure(): Unit = {
    install(new DomainModule)

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

    bind[Dispatcher[Request, Query]].toInstance(pubsub.createDispatcher[Request, Query]())
    bind[TrackQueryHandler].asEagerSingleton()

    bind[Dispatcher[Request, Command]].toInstance(pubsub.createDispatcher[Request, Command]())
    bind[TrackCommandHandler].asEagerSingleton()

    bind[Savepoint].asEagerSingleton()

    bind[ServerBindings].asEagerSingleton()
    bind[EventBus[Event]].toInstance(new EventBus[Event])

    bind[Controller[Track]]
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
  def serverConnectionFactory(userRepository: UserRepository, serverBindings: ServerBindings): ServerInterface = {
    DefaultServer.apply(
      port,
      new DispatchingServerAPI(userRepository, serverBindings)
    )
  }

}
