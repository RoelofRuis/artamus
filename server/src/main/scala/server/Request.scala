package server

import music.model.workspace.User
import pubsub.Dispatchable
import storage.api.DbIO

import scala.reflect.ClassTag

final case class Request[+A : ClassTag](user: User, db: DbIO, attributes: A) extends Dispatchable[A]
