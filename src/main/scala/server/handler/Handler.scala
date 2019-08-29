package server.handler

import protocol.Command

private[server] case class Handler[C <: Command](f: C => Boolean)
