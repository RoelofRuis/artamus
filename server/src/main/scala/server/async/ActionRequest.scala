package server.async

import domain.interact.Event
import domain.workspace.User
import server.async.ActionRegistry.Action
import storage.api.{DbIO, DbResult}

import scala.util.{Failure, Success, Try}

final case class ActionRequest[A](
  user: User,
  db: DbIO,
  attributes: A
)

object ActionRequest {

  def ok: Try[List[Event]] = Success(List())
  def handled[A](res: DbResult[A]): Try[List[Event]] = {
    res match {
      case Right(_) => Success(List())
      case Left(ex) => Failure(ex)
    }
  }

  type ActionHandler[A] = Action[A] => Try[List[Event]]

}

