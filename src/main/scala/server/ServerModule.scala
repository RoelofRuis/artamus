package server

import com.google.inject.Provides
import javax.inject.Singleton
import music.model.write.track.Track
import net.codingwell.scalaguice.ScalaPrivateModule
import protocol._
import pubsub.{Dispatcher, EventBus}
import server.analysis._
import server.analysis.blackboard.Controller
import server.control.{ConnectionLifetimeHooks, DispatchingServerAPI, ServerControlHandler}
import server.domain.ChangeHandler
import server.domain.writing.{TrackCommandHandler, TrackQueryHandler}
import server.interpret.LilypondInterpreter
import server.rendering.{RenderingCompletionHandler, RenderingModule}
import storage.FileStorageModule
import storage.api.DbWithRead

class ServerModule extends ScalaPrivateModule with ServerConfig {

  override def configure(): Unit = {
    install(new FileStorageModule with ServerConfig)

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
