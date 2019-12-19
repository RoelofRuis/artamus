package server.domain.recording

import javax.inject.Inject
import protocol.Command
import pubsub.Dispatcher
import server.Request

private[server] class RecordingHandler @Inject() (
  dispatcher: Dispatcher[Request, Command],
) {

  // TODO: implement

}
