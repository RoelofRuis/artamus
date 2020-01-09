package server.actions.recording

import javax.inject.Inject
import protocol.Command
import pubsub.Dispatcher
import server.{Request, Responses}

private[server] class RecordingCommandHandler @Inject() (
  dispatcher: Dispatcher[Request, Command],
  storage: RecordingStorage,
) {

  dispatcher.subscribe[StartRecording] { req =>
    storage.startRecording(req.user.id, req.attributes.resolution)

    Responses.ok
  }

  dispatcher.subscribe[RecordNote] { req =>
    storage.recordNote(req.user.id, req.attributes.note)

    Responses.ok
  }

}
