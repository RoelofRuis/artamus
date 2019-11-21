package server

import music.domain.user.User
import pubsub.RequestContainer

import scala.reflect.ClassTag

final case class Request[+A : ClassTag](user: User, attributes: A) extends RequestContainer[A]
