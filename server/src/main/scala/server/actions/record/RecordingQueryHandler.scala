package server.actions.record

import domain.interact.Record.GetCurrentRecording
import javax.inject.{Inject, Singleton}
import server.api.QueryDispatcher

import scala.util.{Failure, Success}

@Singleton
private[server] class RecordingQueryHandler @Inject() (
  dispatcher: QueryDispatcher,
  storage: RecordingStorage,
) {

  dispatcher.subscribe[GetCurrentRecording.type] { req =>
    storage.getRecording(req.user.id) match {
      case Some(rec) => Success(rec)
      case None => Failure(new Throwable("No recording for user"))
    }
  }

}
