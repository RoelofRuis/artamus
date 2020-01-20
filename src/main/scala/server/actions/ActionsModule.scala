package server.actions

import net.codingwell.scalaguice.ScalaModule
import server.actions.control.ServerControlHandler
import server.actions.recording.{RecordingCommandHandler, RecordingQueryHandler, RecordingStorage}
import server.actions.writing.{TrackQueryHandler, TrackTaskHandler, TrackUpdateHandler}

class ActionsModule extends ScalaModule {

  override def configure(): Unit = {
    bind[ServerControlHandler].asEagerSingleton()
    bind[TrackQueryHandler].asEagerSingleton()
    bind[TrackUpdateHandler].asEagerSingleton()
    bind[TrackTaskHandler].asEagerSingleton()

    bind[RecordingStorage].asEagerSingleton()
    bind[RecordingCommandHandler].asEagerSingleton()
    bind[RecordingQueryHandler].asEagerSingleton()
  }

}
