package server

import com.google.inject.Provides
import javax.inject.Singleton
import music.model.write.track.Track
import net.codingwell.scalaguice.ScalaPrivateModule
import protocol._
import server.actions.ChangeHandler
import server.actions.control.ServerControlHandler
import server.actions.recording.RecordingCommandHandler
import server.actions.writing.{TrackCommandHandler, TrackQueryHandler}
import server.analysis._
import server.analysis.blackboard.Controller
import server.infra.{ConnectionLifetimeHooks, DispatchingServerAPI, ServerBindings, ServerInfraModule}
import server.interpret.LilypondInterpreter
import server.rendering.{RenderingCompletionHandler, RenderingModule}
import storage.InMemoryStorageModule
import storage.api.DbWithRead

class ServerModule extends ScalaPrivateModule with ServerConfig {

  override def configure(): Unit = {
    install(new InMemoryStorageModule with ServerConfig)
    install(new RenderingModule with ServerConfig)
    install(new ServerInfraModule)

    bind[LilypondInterpreter].toInstance(
      new LilypondInterpreter(
        lyVersion,
        paperSize
      )
    )

    // Handlers
    bind[ServerControlHandler].asEagerSingleton()
    bind[TrackQueryHandler].asEagerSingleton()
    bind[TrackCommandHandler].asEagerSingleton()
    bind[RecordingCommandHandler].asEagerSingleton()
    bind[ChangeHandler].asEagerSingleton()

    bind[RenderingCompletionHandler].to[RenderingEventCompletionHandler]

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
