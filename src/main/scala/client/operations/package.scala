package client

import protocol.v2.Command2

// TODO: see if it can be moved to protocol
package object operations {

  case class OperationToken(command: String, registrar: String)

  type Operation = () => List[Command2]

  trait OperationRegistry {
    def getOperation(token: String): Option[Operation]
    def getRegisteredTokens: Seq[OperationToken]
    def registerOperation(token: OperationToken, operation: Operation): Unit
  }

}
