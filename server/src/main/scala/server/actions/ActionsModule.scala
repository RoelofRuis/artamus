package server.actions

import net.codingwell.scalaguice.ScalaModule
import server.actions.control.ServerControlHandler
import server.actions.perform.PerformanceQueryHandler
import server.actions.record.{RecordingCommandHandler, RecordingQueryHandler, RecordingStorage}
import server.actions.workspace.WorkspaceCommandHandler
import server.actions.write.{TrackTaskHandler, TrackUpdateHandler}

class ActionsModule extends ScalaModule {

  override def configure(): Unit = {
    bind[ServerControlHandler].asEagerSingleton()
    bind[PerformanceQueryHandler].asEagerSingleton()
    bind[TrackUpdateHandler].asEagerSingleton()
    bind[TrackTaskHandler].asEagerSingleton()

    bind[WorkspaceCommandHandler].asEagerSingleton()

    bind[RecordingStorage].asEagerSingleton()
    bind[RecordingCommandHandler].asEagerSingleton()
    bind[RecordingQueryHandler].asEagerSingleton()
  }

}
