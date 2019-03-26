package application

import application.api.{DevicePool, KeyValueStorage, RecordingDevice}
import application.interact.Logger.CmdLogger
import application.interact._
import application.handler._
import application.domain.Idea.Idea_ID
import application.domain.Track.Track_ID
import application.domain.repository.{IdeaRepository, TrackRepository}
import application.domain._
import application.service.quantization.{DefaultQuantizer, TrackQuantizer}
import application.service.recording.RecordingManager
import com.google.inject.Key
import net.codingwell.scalaguice.ScalaPrivateModule

class CoreModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    bind[ApplicationEntryPoint].to[Application].asEagerSingleton()
    expose[ApplicationEntryPoint]

    bind[SynchronousCommandBus].asEagerSingleton()
    bind[DomainEventBus].asEagerSingleton()

    bind[Logger].to[CmdLogger].asEagerSingleton()

    requireBinding(new Key[KeyValueStorage[Idea_ID, Idea]]() {})
    requireBinding(new Key[KeyValueStorage[Track_ID, Track]]() {})
    requireBinding(new Key[RecordingDevice]() {})
    requireBinding(new Key[DevicePool]() {})

    // Configuration
    bind[Int].annotatedWithName("TicksPerQuarter") toInstance 96

    // Services
    bind[TrackQuantizer].toInstance(DefaultQuantizer())
    bind[RecordingManager]

    // Repositories
    bind[IdeaRepository].asEagerSingleton()
    bind[TrackRepository].asEagerSingleton()

    bind[IdeaCommandHandler].asEagerSingleton()
    bind[TrackCommandHandler].asEagerSingleton()
    bind[ApplicationCommandHandler].asEagerSingleton()
  }

}
