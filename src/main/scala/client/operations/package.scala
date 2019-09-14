package client

import protocol.Message

// TODO: see if it can be moved to protocol
package object operations {

  case class OperationToken(command: String, registrar: String)

  type Operation = () => List[Message]

  trait OperationRegistry {
    def getOperation(token: String): Option[Operation]
    def getRegisteredTokens: Iterable[OperationToken]
    def registerOperation(token: OperationToken, operation: Operation): Unit
  }

}
