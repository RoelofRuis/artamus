package server.actions

import net.codingwell.scalaguice.ScalaModule
import server.actions.control.ControlHandler
import server.actions.perform.PerformanceQueryHandler
import server.actions.record.{RecordingCommandHandler, RecordingQueryHandler, RecordingStorage}
import server.actions.workspace.WorkspaceCommandHandler
import server.actions.write.{TrackQueryHandler, TrackTaskHandler, TrackUpdateHandler}

class ActionsModule extends ScalaModule {

  override def configure(): Unit = {
    bind[ControlHandler].asEagerSingleton()
    bind[PerformanceQueryHandler].asEagerSingleton()
    bind[TrackUpdateHandler].asEagerSingleton()
    bind[TrackTaskHandler].asEagerSingleton()
    bind[TrackQueryHandler].asEagerSingleton()

    bind[WorkspaceCommandHandler].asEagerSingleton()

    bind[RecordingStorage].asEagerSingleton()
    bind[RecordingCommandHandler].asEagerSingleton()
    bind[RecordingQueryHandler].asEagerSingleton()
  }

}
