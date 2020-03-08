package server.async

import domain.interact.Event
import domain.workspace.User
import storage.api.{DbIO, DbResult}

import scala.util.{Failure, Success, Try}

final case class CommandRequest[A](
  user: User,
  db: DbIO,
  attributes: A
)

object CommandRequest {

  def ok: Try[List[Event]] = Success(List())
  def handled[A](res: DbResult[A]): Try[List[Event]] = {
    res match {
      case Right(_) => Success(List())
      case Left(ex) => Failure(ex)
    }
  }

  type CommandHandler[A] = CommandRequest[A] => Try[List[Event]]

}

