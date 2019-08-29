package server.api

import scala.util.Try

package object messages {

  // TODO: see if this needs to be moved to a shared protocol folder
  sealed trait ServerRequestMessage
  case object ControlMessage extends ServerRequestMessage
  case object CommandMessage extends ServerRequestMessage

  sealed trait ServerResponseMessage
  case object ResponseMessage extends ServerResponseMessage

  trait Event

  trait Control

  case class Disconnect(shutdownServer: Boolean) extends Control

  trait Command {
    type Res
  }

  private[server] case class Handler[C <: Command](f: C => Try[C#Res])

}
