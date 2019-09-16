package client.operations

class ClientOperationRegistry extends OperationRegistry {

  private var operations: Map[OperationToken, Operation] = Map[OperationToken, Operation]()

  override def registerOperation(token: OperationToken, operation: Operation): Unit = operations += (token -> operation)

  override def getRegisteredTokens: Seq[OperationToken] = operations.keys.toSeq

  override def getOperation(command: String): Option[Operation] = operations.map { case (token, opp) => token.command -> opp }.get(command)

}
