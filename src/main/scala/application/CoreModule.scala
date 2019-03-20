package application

import application.component.ServiceRegistry.Settings
import application.component._
import application.handler._
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
    expose[ApplicationEntryPoint]

    bind[SynchronizedMessageBus].asEagerSingleton()
    bind[ResourceManager].asEagerSingleton()
    bind[Logger].asEagerSingleton()

    requireBinding(new Key[KeyValueStorage[Idea_ID, Idea]]() {})
    requireBinding(new Key[KeyValueStorage[Track_ID, Track]]() {})

    bind[Settings[RecordingDevice]].toInstance(Settings[RecordingDevice](allowsMultiple = false))
    bind[ServiceRegistry[RecordingDevice]].asEagerSingleton()

    // Configuration
    bind[Int].annotatedWithName("TicksPerQuarter") toInstance 96

    // Services
    bind[TrackQuantizer].toInstance(DefaultQuantizer())
    bind[RecordingManager]

    // Repositories
    bind[IdeaRepository].asEagerSingleton()
    bind[TrackRepository].asEagerSingleton()

    val controllers = ScalaMultibinder.newSetBinder[CommandHandler](binder)
    controllers.addBinding.to[IdeaCommandHandler]
    controllers.addBinding.to[ResourceCommandHandler]
    controllers.addBinding.to[TrackCommandHandler]
    controllers.addBinding.to[ApplicationCommandHandler]
  }

}
