package server.actions

import net.codingwell.scalaguice.ScalaModule
import server.actions.control.ServerControlHandler
import server.actions.recording.{RecordingCommandHandler, RecordingQueryHandler, RecordingStorage}
import server.actions.writing.{TrackQueryHandler, TrackTaskHandler, TrackUpdateHandler}

class ActionsModule extends ScalaModule {

  override def configure(): Unit = {
    bind[ServerControlHandler]
    bind[TrackQueryHandler]
    bind[TrackUpdateHandler]
    bind[TrackTaskHandler]

    bind[RecordingStorage]
    bind[RecordingCommandHandler]
    bind[RecordingQueryHandler]
  }

}
