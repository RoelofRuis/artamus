package server.domain.recording

import javax.inject.Inject
import protocol.Command
import pubsub.Dispatcher
import server.{Request, Responses}

private[server] class RecordingCommandHandler @Inject() (
  dispatcher: Dispatcher[Request, Command],
) {

  import server.model.Workspaces._

  dispatcher.subscribe[StartRecording] { req =>
    Responses.ok
  }

}
