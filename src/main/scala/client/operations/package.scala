package client

import protocol.Command

import scala.util.{Success, Try}

// TODO: see if it can be moved to protocol
package object operations {

  case class OperationToken(command: String, registrar: String)

  type Operation = () => Try[List[Command]]

  object Operation {
    def list(commands: Command*): Try[List[Command]] = Success(commands.toList)
    def none: Try[List[Command]] = Success(List())
  }

  trait OperationRegistry {
    def getOperation(token: String): Option[Operation]
    def getRegisteredTokens: Seq[OperationToken]
    def registerOperation(token: OperationToken, operation: Operation): Unit
  }

}
