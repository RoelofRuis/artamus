package client.operations

class ClientOperationRegistry extends OperationRegistry {

  private var operations: Map[OperationToken, Operation] = Map[OperationToken, Operation]()

  override def registerOperation(token: OperationToken, operation: Operation): Unit = operations += (token -> operation)

  override def getRegisteredTokens: Iterable[OperationToken] = operations.keys

  override def getOperation(command: String): Option[Operation] = operations.map { case (token, opp) => token.command -> opp }.get(command)

}
