package server.api

import domain.interact.Event
import domain.workspace.User
import storage.api.{DbIO, DbResult, IOError, NotFound}

import scala.util.{Failure, Success, Try}

private[server] final case class CommandRequest[A](
  user: User,
  db: DbIO,
  attributes: A
)

object CommandRequest {

  def ok: Try[List[Event]] = Success(List())
  def okWithEvents(events: List[Event]): Try[List[Event]] = Success(events)
  def dbResult(res: DbResult[_]): Try[List[Event]] = {
    res match {
      case Right(_) => Success(List())
      case Left(IOError(cause)) => Failure(cause)
      case Left(NotFound()) => Failure(new Throwable("Unable to find model"))
    }
  }
  def dbResultWithEvents(res: DbResult[List[Event]]): Try[List[Event]] = {
    res match {
      case Right(list) => Success(list)
      case Left(IOError(cause)) => Failure(cause)
      case Left(NotFound()) => Failure(new Throwable("Unable to find model"))
    }
  }

  type CommandHandler[A] = CommandRequest[A] => Try[List[Event]]

}

