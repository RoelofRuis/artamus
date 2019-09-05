package client

import protocol.{Command, Control}

package object operations {

  // TODO: rethink this completely
  trait Operation {
    def getControl: List[Control]
    def getCommands: List[Command]
  }

  trait OperationRegistry {
    def getOperation(token: String): Option[Operation]
  }

}
