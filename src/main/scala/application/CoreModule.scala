package application

import application.component.Logger.CmdLogger
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
    bind[DomainEventBus].asEagerSingleton()

    bind[Logger].to[CmdLogger].asEagerSingleton()

    requireBinding(new Key[KeyValueStorage[Idea_ID, Idea]]() {})
    requireBinding(new Key[KeyValueStorage[Track_ID, Track]]() {})
    requireBinding(new Key[RecordingDevice]() {})

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
    handlers.addBinding.to[TrackCommandHandler]
    handlers.addBinding.to[ApplicationCommandHandler]
  }

}
