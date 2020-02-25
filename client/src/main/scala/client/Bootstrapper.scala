package client

import client.events.RenderHandler
import client.infra.{Client, ClientInteraction}
import client.module.Operations.{LocalOperation, Operation, OperationRegistry, ServerOperation}
import com.typesafe.scalalogging.LazyLogging
import domain.interact.Command
import domain.interact.Control.Authenticate
import javax.inject.Inject

import scala.annotation.tailrec
import scala.util.{Failure, Success}

class Bootstrapper @Inject() (
  client: Client,
  registry: OperationRegistry,
  renderHandler: RenderHandler,
  moduleLifetimeHooks: ModuleLifetimeHooks // Expand only as more hooks are needed
) extends LazyLogging {

  import ClientInteraction._

  def run(): Unit = {
    moduleLifetimeHooks.initializeAll()
    var isRunning = true

    tryAuthenticate()

    while (isRunning) {
      nextOperation match {
        case ("quit", _) => isRunning = false
        case (_, op: LocalOperation) =>
          op.f() match {
            case Success(()) =>
            case Failure(ex) => logger.error("Error when fetching operations", ex)
          }

        case (_, op: ServerOperation) =>
          op.f() match {
            case Success(commands) => sendCommands(commands)
            case Failure(ex) => logger.error("Error when fetching operations", ex)
          }
      }
    }

    moduleLifetimeHooks.closeAll()
    renderHandler.frame.dispose()
  }

  private def tryAuthenticate(): Unit = client.sendCommand(Authenticate("artamus"))

  @tailrec
  private def sendCommands(commands: List[Command]): Unit = commands match {
    case Nil =>
    case command :: rest =>
      client.sendCommand(command) match {
        case None => sendCommands(rest)
        case _ =>
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