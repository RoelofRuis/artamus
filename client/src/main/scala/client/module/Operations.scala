package client.module

import domain.interact.Command

import scala.util.{Success, Try}

object Operations {

  final case class OperationToken(command: String, registrar: String)

  sealed trait Operation
  case class LocalOperation(f: () => Try[Unit]) extends Operation
  case class ServerOperation(f: () => Try[List[Command]]) extends Operation

  object ServerOperation {
    def apply(commands: Command*): Try[List[Command]] = Success(commands.toList)
  }

  trait OperationRegistry {
    def getOperation(token: String): Option[Operation]
    def viewRegisteredTokens: Seq[OperationToken]
    def registerOperation(token: OperationToken, operation: Operation): Unit

    def local(command: String, registrar: String, f: => Unit): Unit =
      registerOperation(OperationToken(command, registrar), LocalOperation(() => Try(f)))

    def server(command: String, registrar: String, f: => Try[List[Command]]): Unit =
      registerOperation(OperationToken(command, registrar), ServerOperation(() => f))
  }

}
