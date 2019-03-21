package application

import application.channels.{Channel, Logging, Playback}
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
    requireBinding(new Key[RecordingDevice]() {})

    val loggingChannel = new Key[Channel[Logging.type]]() {}
    bind(loggingChannel).asEagerSingleton()
    expose(loggingChannel)

    val playbackChannel = new Key[Channel[Playback.type]]() {}
    bind(playbackChannel).asEagerSingleton()
    expose(playbackChannel)

    // Configuration
    bind[Int].annotatedWithName("TicksPerQuarter") toInstance 96

    // Services
    bind[TrackQuantizer].toInstance(DefaultQuantizer())
    bind[RecordingManager]

    // Repositories
    bind[IdeaRepository].asEagerSingleton()
    bind[TrackRepository].asEagerSingleton()

    val handlers = ScalaMultibinder.newSetBinder[CommandHandler](binder)
    handlers.addBinding.to[IdeaCommandHandler]
    handlers.addBinding.to[ResourceCommandHandler]
    handlers.addBinding.to[TrackCommandHandler]
    handlers.addBinding.to[ApplicationCommandHandler]
  }

}
