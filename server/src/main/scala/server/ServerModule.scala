package server

import music.model.write.track.Track
import net.codingwell.scalaguice.ScalaPrivateModule
import server.actions.ActionsModule
import server.analysis.blackboard.Controller
import server.analysis.{ChordAnalyser, PitchHistogramAnalyser}
import server.infra.ServerInfraModule
import server.rendering.RenderingModule
import storage.InMemoryStorageModule

class ServerModule extends ScalaPrivateModule with ServerSettings {

  override def configure(): Unit = {
    // -- pick a storage module
    // install(new FileStorageModule with ServerConfig)
    install(new InMemoryStorageModule)
    // --

    install(new ServerInfraModule)
    install(new RenderingModule with ServerSettings)
    install(new ActionsModule)

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
