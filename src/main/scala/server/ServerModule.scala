package server

import _root_.server.analysis._
import _root_.server.control.{ConnectionLifetimeHooks, DispatchingServerAPI, ServerControlHandler}
import _root_.server.domain.track.{TrackCommandHandler, TrackQueryHandler}
import com.google.inject.Provides
import javax.inject.Singleton
import music.domain.write.track.Track
import net.codingwell.scalaguice.ScalaPrivateModule
import protocol._
import pubsub.{Dispatcher, EventBus}
import server.analysis.blackboard.Controller
import server.domain.ChangeHandler
import server.interpret.LilypondInterpreter
import server.rendering.{RenderingCompletionHandler, RenderingModule}
import _root_.storage.InMemoryStorageModule
import storage.api.DbWithRead

class ServerModule extends ScalaPrivateModule with ServerConfig {

  override def configure(): Unit = {
    install(new InMemoryStorageModule)

    bind[LilypondInterpreter].toInstance(
      new LilypondInterpreter(
        lyVersion,
        paperSize
      )
    )

    bind[ConnectionLifetimeHooks].asEagerSingleton()

    bind[RenderingCompletionHandler].to[RenderingEventCompletionHandler]
    install(new RenderingModule with ServerConfig)

    bind[ServerControlHandler].asEagerSingleton()
    bind[ChangeHandler].asEagerSingleton()

    bind[Dispatcher[Request, Query]].toInstance(pubsub.createDispatcher[Request, Query]())
    bind[TrackQueryHandler].asEagerSingleton()

    bind[Dispatcher[Request, Command]].toInstance(pubsub.createDispatcher[Request, Command]())
    bind[TrackCommandHandler].asEagerSingleton()

    bind[ServerBindings].asEagerSingleton()
    bind[EventBus[Event]].toInstance(new EventBus[Event])

    bind[Controller[Track]]
      .toInstance(new Controller(
        Seq(
          new ChordAnalyser(),
          new PitchHistogramAnalyser()
        ),
      ))

    bind[Bootstrapper].asEagerSingleton()
    expose[Bootstrapper]
  }

  @Provides @Singleton
  def serverConnectionFactory(
    db: DbWithRead,
    serverBindings: ServerBindings,
    hooks: ConnectionLifetimeHooks
  ): ServerInterface = {
    DefaultServer.apply(
      port,
      new DispatchingServerAPI(db, serverBindings, hooks)
    )
  }

}
