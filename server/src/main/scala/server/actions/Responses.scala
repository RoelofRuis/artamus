package server.actions

import storage.api.DbResult

import scala.util.{Success, Try}

object Responses {
  def ok: Success[Unit] = Success(())
  def executed(res: DbResult[Unit]): Try[Unit] = res.toTry
  def returning[A](res: DbResult[A], default: Option[A] = None): Try[A] = {
    val withDefault = default match {
      case Some(d) => res.ifNotFound(d)
      case None => res
    }
    withDefault.toTry
  }
}
