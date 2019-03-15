package application

import application.component.ServiceRegistry.Settings
import application.component.{Application, ResourceManager, ServiceRegistry}
import application.controller._
import application.model.Idea.Idea_ID
import application.model.Track.Track_ID
import application.model.repository.{IdeaRepository, TrackRepository}
import application.model._
import application.ports._
import application.quantization.{DefaultQuantizer, TrackQuantizer}
import com.google.inject.Key
import net.codingwell.scalaguice.ScalaPrivateModule

class CoreModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    bind[ApplicationEntryPoint].to[Application].asEagerSingleton()
    expose[ApplicationEntryPoint]

    bind[ResourceManager].asEagerSingleton()
    requireBinding(new Key[Driver]() {})

    requireBinding(new Key[KeyValueStorage[Idea_ID, Idea]]() {})
    requireBinding(new Key[KeyValueStorage[Track_ID, Track]]() {})

    bind[Settings[PlaybackDevice]].toInstance(Settings[PlaybackDevice](allowsMultiple = true))
    bind[ServiceRegistry[PlaybackDevice]].asEagerSingleton()
    bind[Settings[InputDevice]].toInstance(Settings[InputDevice](allowsMultiple = false))
    bind[ServiceRegistry[InputDevice]].asEagerSingleton()
    bind[Settings[Logger]].toInstance(Settings[Logger](allowsMultiple = true))
    bind[ServiceRegistry[Logger]].asEagerSingleton()

    // Configuration
    bind[TicksPerQuarter].toInstance(TicksPerQuarter(96))

    // Services
    bind[TrackQuantizer].toInstance(DefaultQuantizer())

    // Repositories
    bind[IdeaRepository].asEagerSingleton()
    bind[TrackRepository].asEagerSingleton()

    // Controllers
    bind[ResourceController].to[ResourceControllerImpl].asEagerSingleton()
    expose[ResourceController]

    bind[IdeaController].to[IdeaControllerImpl].asEagerSingleton()
    expose[IdeaController]

    bind[TrackController].to[TrackControllerImpl].asEagerSingleton()
    expose[TrackController]

    bind[ServiceController[Logger]].to[ServiceControllerImpl[Logger]].asEagerSingleton()
    expose[ServiceController[Logger]]

    bind[ServiceController[PlaybackDevice]].to[ServiceControllerImpl[PlaybackDevice]].asEagerSingleton()
    expose[ServiceController[PlaybackDevice]]

    bind[ServiceController[InputDevice]].to[ServiceControllerImpl[InputDevice]]asEagerSingleton()
    expose[ServiceController[InputDevice]]
  }

}
