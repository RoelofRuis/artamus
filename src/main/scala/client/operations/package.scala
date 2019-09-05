package client

import protocol.Message

// TODO: see if it can be moved to protocol
package object operations {

  type Operation = () => List[Message]

  trait OperationRegistry {
    def getOperation(token: String): Option[Operation]
    def registerOperation(token: String, operation: Operation): Unit
  }

}
