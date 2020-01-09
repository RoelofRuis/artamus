package client

import client.events.RenderHandler
import client.io.IOLifetimeManager
import client.operations.Operations.{LocalOperation, Operation, OperationRegistry, ServerOperation}
import javax.inject.Inject
import protocol.Command
import protocol.client.api.ClientInterface
import server.actions.control.Authenticate

import scala.annotation.tailrec
import scala.util.{Failure, Success}

class Bootstrapper @Inject() (
  client: ClientInterface,
  registry: OperationRegistry,
  renderHandler: RenderHandler,
  ioManager: IOLifetimeManager
) {

  def run(): Unit = {
    ioManager.initializeAll()
    var isRunning = true

    tryAuthenticate()

    while (isRunning) {
      nextOperation match {
        case ("quit", _) => isRunning = false
        case (_, op: LocalOperation) =>
          op.f() match {
            case Success(()) =>
            case Failure(ex) =>
              println("Error when running command:")
              ex.printStackTrace()
          }

        case (_, op: ServerOperation) =>
          op.f() match {
            case Success(commands) => sendCommands(commands)
            case Failure(ex) =>
              println("Unable to get commands:")
              ex.printStackTrace()
          }
      }
    }

    ioManager.closeAll()
    renderHandler.frame.dispose()
  }

  private def tryAuthenticate(): Unit = {
    client.sendCommand(Authenticate("artamus"))
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

  @tailrec
  private def nextOperation: (String, Operation) = {
    println("Input next command:")
    val input = scala.io.StdIn.readLine()
    registry.getOperation(input) match {
      case Some(op) => (input, op)
      case None =>
        println(s"Unknown command [$input]")
        nextOperation
    }
  }

}
