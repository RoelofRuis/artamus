package server

import music.model.write.user.User
import pubsub.RequestContainer
import storage.api.DbIO

import scala.reflect.ClassTag

final case class Request[+A : ClassTag](user: User, db: DbIO, attributes: A) extends RequestContainer[A]
