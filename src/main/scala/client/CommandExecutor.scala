package client

import client.operations.Operations.{LocalOperation, OperationRegistry, ServerOperation}
import javax.inject.Inject
import protocol.Command
import protocol.client.api.ClientInterface

import scala.annotation.tailrec
import scala.util.{Failure, Success}

class CommandExecutor @Inject() (
  client: ClientInterface,
  registry: OperationRegistry,
) {

  def execute(input: String): Boolean = {
    registry.getOperation(input) match {
      case None => false
      case Some(op: LocalOperation) =>
        op.f() match {
          case Success(()) =>
          case Failure(ex) =>
            println("Error when running command:")
            ex.printStackTrace()
        }
        true

      case Some(op: ServerOperation) =>
        op.f() match {
          case Success(commands) => sendCommands(commands)
          case Failure(ex) =>
            println("Unable to get commands:")
            ex.printStackTrace()
        }
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
