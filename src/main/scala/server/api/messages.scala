package server.api

import scala.util.Try

package object messages {

  // TODO: create protocol for outgoing messages so they can be 'split' on the receiving side.
  sealed trait MessageType
  case object CommandMessage extends MessageType

  trait Command {
    type Res
  }

  private[server] case class Handler[C <: Command](f: C => Try[C#Res])

}
