package server

import music.domain.user.User
import pubsub.RequestContainer
import server.storage.api.DbIO

import scala.reflect.ClassTag

final case class Request[+A : ClassTag](user: User, db: DbIO, attributes: A) extends RequestContainer[A]
