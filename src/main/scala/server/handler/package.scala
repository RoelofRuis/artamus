package server

import protocol.{Command, Query}

package object handler {

  final case class CommandHandler[C <: Command](f: C => Boolean)

  final case class QueryHandler[Q <: Query](f: Q => Q#Res)

}
