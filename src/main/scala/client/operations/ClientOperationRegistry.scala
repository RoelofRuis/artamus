package client.operations

class ClientOperationRegistry extends OperationRegistry {

  private var operations: Map[String, Operation] = Map[String, Operation]()

  override def registerOperation(token: String, operation: Operation): Unit = operations += (token -> operation)

  override def getOperation(token: String): Option[Operation] = operations.get(token)

}
