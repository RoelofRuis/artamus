package client.module

import client.module.ClientOperationRegistry.InvalidRegistrationException
import client.module.Operations.{Operation, OperationRegistry, OperationToken}

class ClientOperationRegistry extends OperationRegistry {

  private var operations: Map[OperationToken, Operation] = Map[OperationToken, Operation]()

  override def registerOperation(token: OperationToken, operation: Operation): Unit = {
    operations.find { case (t, _) => t.command == token.command} match {
      case None => operations += (token -> operation)
      case Some(existing) =>
        val existingToken = existing._1
        throw InvalidRegistrationException(
          s"Duplicate command registration with name [${existingToken.command}] (by [${existingToken.registrar}] and [${token.registrar}])"
        )
    }
  }

  override def viewRegisteredTokens: Seq[OperationToken] = operations.keys.toSeq

  override def getOperation(command: String): Option[Operation] = operations.map { case (token, opp) => token.command -> opp }.get(command)

}

object ClientOperationRegistry {

  case class InvalidRegistrationException(msg: String) extends Exception(msg)

}