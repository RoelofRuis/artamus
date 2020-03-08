package server.api

import domain.interact.Event
import domain.workspace.User
import network.Exceptions.LogicError
import storage.api.{DbIO, DbResult, IOError, NotFound}

import scala.util.{Failure, Success, Try}

private[server] final case class CommandRequest[A](
  user: User,
  db: DbIO,
  attributes: A
)

object CommandRequest {

  def ok: Try[List[Event]] = Success(List())
  def dbResult(res: DbResult[_]): Try[List[Event]] = {
    res match {
      case Right(_) => Success(List())
      case Left(IOError(cause)) => Failure(cause)
      case Left(NotFound()) => Failure(LogicError) // TODO: this should be a better error!
    }
  }
  def dbResultWithEvents(res: DbResult[List[Event]]): Try[List[Event]] = {
    res match {
      case Right(list) => Success(list)
      case Left(IOError(cause)) => Failure(cause)
      case Left(NotFound()) => Failure(LogicError) // TODO: this should be a better error!
    }
  }

  type CommandHandler[A] = CommandRequest[A] => Try[List[Event]]

}

