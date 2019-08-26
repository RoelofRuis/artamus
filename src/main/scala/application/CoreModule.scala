package application

import application.api.Commands.TrackID
import application.api.{DevicePool, KeyValueStorage, RecordingDevice}
import application.handler._
import application.interact.Logger.CmdLogger
import application.interact._
import application.model.Track
import application.repository.TrackRepository
import application.service.quantization.{DefaultQuantizer, TrackQuantizer}
import application.service.recording.RecordingManager
import com.google.inject.Key
import net.codingwell.scalaguice.ScalaPrivateModule

class CoreModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    bind[ApplicationEntryPoint].to[Application].asEagerSingleton()
    expose[ApplicationEntryPoint]

    bind[Logger].toInstance(new CmdLogger(true))

    bind[SocketCommandBus].asEagerSingleton()
    bind[ApplicationEventBus].asEagerSingleton()

    requireBinding(new Key[KeyValueStorage[TrackID, Track]]() {})

    requireBinding(new Key[RecordingDevice]() {})
    requireBinding(new Key[DevicePool]() {})

    // Configuration
    bind[Int].annotatedWithName("TicksPerQuarter") toInstance 96

    // Services
    bind[TrackQuantizer].toInstance(DefaultQuantizer())
    bind[RecordingManager]

    // Repositories
    bind[TrackRepository].asEagerSingleton()

    bind[TrackCommandHandler].asEagerSingleton()
    bind[ApplicationCommandHandler].asEagerSingleton()
  }

}
