package server

import music.model.write.track.Track
import net.codingwell.scalaguice.ScalaPrivateModule
import server.actions.control.ServerControlHandler
import server.actions.recording.RecordingCommandHandler
import server.actions.writing.{TrackQueryHandler, TrackTaskHandler, TrackUpdateHandler}
import server.analysis.blackboard.Controller
import server.analysis.{ChordAnalyser, PitchHistogramAnalyser}
import server.infra.ServerInfraModule
import server.rendering.RenderingModule
import storage.InMemoryStorageModule

class ServerModule extends ScalaPrivateModule with ServerSettings {

  override def configure(): Unit = {
    // -- pick either one storage
    // install(new FileStorageModule with ServerConfig)
    install(new InMemoryStorageModule)
    // --

    install(new RenderingModule with ServerSettings)
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

}
