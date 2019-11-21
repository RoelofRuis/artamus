package server

import music.domain.user.User

final case class Request[+A](user: User, data: A)
