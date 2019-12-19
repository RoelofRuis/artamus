package client

import client.operations.OperationRegistry
import javax.inject.Inject
import protocol.{ClientInterface, Command}

import scala.annotation.tailrec

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
        sendCommands(op())
        true
    }
  }

  @tailrec
  private def sendCommands(commands: List[Command]): Unit = commands match {
    case Nil =>
    case command :: rest =>
      if (client.sendCommand(command)) {
        println(s"[$command] executed")
        sendCommands(rest)
      } else {
        println(s"[$command] failed! (skipping [${rest.length}] more)")
      }
  }

  def exit(): Unit = client.close()

}
