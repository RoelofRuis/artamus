package server

import com.google.inject.Provides
import javax.inject.Singleton
import music.model.write.track.Track
import net.codingwell.scalaguice.ScalaPrivateModule
import protocol._
import server.actions.control.ServerControlHandler
import server.actions.recording.RecordingCommandHandler
import server.actions.writing.{TrackQueryHandler, TrackTaskHandler, TrackUpdateHandler}
import server.analysis._
import server.analysis.blackboard.Controller
import server.infra.{ConnectionLifetimeHooks, DispatchingServerAPI, ServerBindings, ServerInfraModule}
import server.rendering.RenderingModule
import storage.InMemoryStorageModule
import storage.api.DbWithRead

class ServerModule extends ScalaPrivateModule with ServerConfig {

  override def configure(): Unit = {
    // -- pick either one storage
    // install(new FileStorageModule with ServerConfig)
    install(new InMemoryStorageModule)
    // --

    install(new RenderingModule with ServerConfig)
    install(new ServerInfraModule)

    // Handlers
    bind[ServerControlHandler].asEagerSingleton()
    bind[TrackQueryHandler].asEagerSingleton()
    bind[TrackUpdateHandler].asEagerSingleton()
    bind[TrackTaskHandler].asEagerSingleton()
    bind[RecordingCommandHandler].asEagerSingleton()

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
