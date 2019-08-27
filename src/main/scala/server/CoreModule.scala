package server

import server.Logger.CmdLogger
import server.api.Actions.TrackID
import server.api.KeyValueStorage
import server.handler._
import server.interact._
import server.model.Track
import server.repository.TrackRepository
import server.service.quantization.{DefaultQuantizer, TrackQuantizer}
import com.google.inject.Key
import net.codingwell.scalaguice.ScalaPrivateModule

class CoreModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    bind[Logger].toInstance(new CmdLogger(true))
    expose[Logger]

    bind[SocketCommandBus].asEagerSingleton()
    expose[SocketCommandBus]
    bind[ApplicationEventBus].asEagerSingleton()

    requireBinding(new Key[KeyValueStorage[TrackID, Track]]() {})

    // Configuration
    bind[Int].annotatedWithName("TicksPerQuarter") toInstance 96

    // Services
    bind[TrackQuantizer].toInstance(DefaultQuantizer())

    // Repositories
    bind[TrackRepository].asEagerSingleton()

    bind[TrackCommandHandler].asEagerSingleton()
    bind[ApplicationCommandHandler].asEagerSingleton()
  }

}
