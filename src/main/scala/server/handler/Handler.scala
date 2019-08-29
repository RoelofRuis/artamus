package server.handler

import protocol.Command

import scala.util.Try

private[server] case class Handler[C <: Command](f: C => Try[C#Res])
