package server.actions.record

import domain.interact.Record.GetCurrentRecording
import javax.inject.{Inject, Singleton}
import server.infra.ServerDispatcher

import scala.util.{Failure, Success}

@Singleton
private[server] class RecordingQueryHandler @Inject() (
  dispatcher: ServerDispatcher,
  storage: RecordingStorage,
) {

  dispatcher.subscribe[GetCurrentRecording.type] { req =>
    storage.getRecording(req.user.id) match {
      case Some(rec) => Success(rec)
      case None => Failure(new Throwable("No recording for user"))
    }
  }

}
