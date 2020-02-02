package server

import domain.workspace.User
import pubsub.Dispatchable
import storage.api.DbIO

import scala.reflect.ClassTag

final case class ServerRequest[+A : ClassTag](user: User, db: DbIO, attributes: A) extends Dispatchable[A]
