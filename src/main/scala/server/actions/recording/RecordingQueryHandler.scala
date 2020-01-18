package server.actions.recording

import javax.inject.Inject
import protocol.Query
import pubsub.Dispatcher
import server.Request

import scala.util.{Failure, Success}

private[server] class RecordingQueryHandler @Inject() (
  dispatcher: Dispatcher[Request, Query],
  storage: RecordingStorage,
) {

  dispatcher.subscribe[GetCurrentRecording.type] { req =>
    storage.getRecording(req.user.id) match {
      case Some(rec) => Success(rec)
      case None => Failure(new Throwable("No recording for user"))
    }
  }

}
