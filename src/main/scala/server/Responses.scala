package server

import storage.api.ModelIO.{ModelResult, NotFound}

import scala.util.{Failure, Success, Try}

object Responses {
  def ok: Success[Unit] = Success(())
  def executed(res: ModelResult[Unit]): Try[Unit] = res.toTry
  def returning[A](res: ModelResult[A], default: Option[A] = None): Try[A] = res match {
    case Right(a) => Success(a)
    case Left(_: NotFound) if default.isDefined => Success(default.get)
    case Left(ex) => Failure(ex)
  }
}
