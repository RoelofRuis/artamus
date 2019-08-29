package server.api

import scala.util.Try

package object messages {

  sealed trait IncomingMessageType
  case object CommandMessage extends IncomingMessageType

  sealed trait OutgoingMessageType
  case object ResponseMessage extends OutgoingMessageType
  case object EventMessage extends OutgoingMessageType


  trait Command {
    type Res
  }

  private[server] case class Handler[C <: Command](f: C => Try[C#Res])

}
