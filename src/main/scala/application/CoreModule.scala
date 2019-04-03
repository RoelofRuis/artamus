package application

import application.api.{DevicePool, KeyValueStorage, RecordingDevice}
import application.handler._
import application.interact.Logger.CmdLogger
import application.interact._
import application.model.event.MidiTrack
import application.model.event.MidiTrack.Track_ID
import application.model.symbolic.Track
import application.repository.TrackRepository
import application.service.SymbolTrackFactory
import application.service.quantization.{DefaultQuantizer, TrackQuantizer}
import application.service.recording.RecordingManager
import com.google.inject.Key
import net.codingwell.scalaguice.ScalaPrivateModule

class CoreModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    bind[ApplicationEntryPoint].to[Application].asEagerSingleton()
    expose[ApplicationEntryPoint]

    bind[SynchronousCommandBus].asEagerSingleton()
    bind[ApplicationEventBus].asEagerSingleton()

    bind[Logger].toInstance(new CmdLogger(false))

    requireBinding(new Key[KeyValueStorage[Track.TrackID, Track]]() {})

    requireBinding(new Key[KeyValueStorage[Track_ID, MidiTrack]]() {})
    requireBinding(new Key[RecordingDevice]() {})
    requireBinding(new Key[DevicePool]() {})

    // Configuration
    bind[Int].annotatedWithName("TicksPerQuarter") toInstance 96

    // Services
    bind[TrackQuantizer].toInstance(DefaultQuantizer())
    bind[SymbolTrackFactory].asEagerSingleton()
    bind[RecordingManager]

    // Repositories
    bind[TrackRepository].asEagerSingleton()

    bind[TrackCommandHandler].asEagerSingleton()
    bind[ApplicationCommandHandler].asEagerSingleton()
  }

}
