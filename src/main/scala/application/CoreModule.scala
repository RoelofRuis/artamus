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
import application.recording.RecordingManager
import com.google.inject.Key
import net.codingwell.scalaguice.{ScalaMultibinder, ScalaPrivateModule}

class CoreModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    bind[ApplicationEntryPoint].to[Application].asEagerSingleton()
    bind[MessageBus].asEagerSingleton()
    expose[ApplicationEntryPoint]
    expose[MessageBus]

    bind[ResourceManager].asEagerSingleton()
    requireBinding(new Key[Driver]() {})

    requireBinding(new Key[KeyValueStorage[Idea_ID, Idea]]() {})
    requireBinding(new Key[KeyValueStorage[Track_ID, Track]]() {})

    bind[Settings[PlaybackDevice]].toInstance(Settings[PlaybackDevice](allowsMultiple = true))
    bind[ServiceRegistry[PlaybackDevice]].asEagerSingleton()
    bind[Settings[RecordingDevice]].toInstance(Settings[RecordingDevice](allowsMultiple = false))
    bind[ServiceRegistry[RecordingDevice]].asEagerSingleton()

    requireBinding(new Key[Logger]() {})

    // Configuration
    bind[Int].annotatedWithName("TicksPerQuarter") toInstance 96

    // Services
    bind[TrackQuantizer].toInstance(DefaultQuantizer())
    bind[RecordingManager]

    // Repositories
    bind[IdeaRepository].asEagerSingleton()
    bind[TrackRepository].asEagerSingleton()

    val controllers = ScalaMultibinder.newSetBinder[Controller](binder)
    controllers.addBinding.to[IdeaController]
    controllers.addBinding.to[ResourceController]
    controllers.addBinding.to[TrackController]
  }

}
