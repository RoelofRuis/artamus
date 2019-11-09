package client

import client.operations.OperationRegistry
import javax.inject.Inject
import protocol.ClientInterface

class CommandExecutor @Inject() (
  client: ClientInterface,
  registry: OperationRegistry,
) {

  client.open()

  def execute(input: String): Boolean = {
    registry.getOperation(input) match {
      case None => false
      case Some(op) =>
        // TODO: improve user feedback
        op().map(client.sendCommand)
        true
    }
  }

  def exit(): Unit = client.close()

}
