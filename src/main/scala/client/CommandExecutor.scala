package client

import client.operations.OperationRegistry
import javax.inject.Inject
import protocol.Command
import protocol.client.api.ClientInterface

import scala.annotation.tailrec

class CommandExecutor @Inject() (
  client: ClientInterface,
  registry: OperationRegistry,
) {

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
      client.sendCommand(command) match {
        case None =>
          println(s"[$command] executed")
          sendCommands(rest)
        case Some(ex) =>
          println(s"[$command] failed")
          println(s"cause [${ex.name}: ${ex.description}]")
          ex.cause.foreach(_.printStackTrace)
          println(s"skipping [${rest.length}] more")
      }
  }

}
