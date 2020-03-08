package server.api

import domain.workspace.User
import pubsub.Dispatchable
import storage.api.{DbReader, DbResult}

import scala.reflect.ClassTag
import scala.util.Try

private[server] final case class QueryRequest[+A: ClassTag](
  user: User,
  db: DbReader,
  attributes: A
) extends Dispatchable[A]

object QueryRequest {

  def returning[A](res: DbResult[A], default: Option[A] = None): Try[A] = {
    val withDefault = default match {
      case Some(d) => res.ifNotFound(d)
      case None => res
    }
    withDefault.toTry
  }

}