package server.actions.recording

import javax.inject.Inject
import music.model.record.Recording
import protocol.v2.Command2
import pubsub.Dispatcher
import server.{Request, Responses}
import storage.api.DbResult

private[server] class RecordingCommandHandler @Inject() (
  dispatcher: Dispatcher[Request, Command2],
) {

  import server.model.Recordings._
  import server.model.Workspaces._

  dispatcher.subscribe[StartRecording] { req =>
    val res = for {
      workspace <- req.db.getWorkspaceByOwner(req.user)
      recording = Recording(req.attributes.resolution)
      newWorkspace = workspace.startRecording(recording)
      _ <- req.db.saveWorkspace(newWorkspace)
      _ <- req.db.saveRecording(recording)
    } yield ()

    Responses.executed(res)
  }

  dispatcher.subscribe[RecordNote] { req =>
    val res = for {
      workspace <- req.db.getWorkspaceByOwner(req.user)
      recording <- workspace.activeRecording.map(req.db.getRecordingById).getOrElse(DbResult.notFound)
      newRecording = recording.recordNote(req.attributes.note)
      _ <- req.db.saveRecording(newRecording)
    } yield ()

    Responses.executed(res)
  }

}
