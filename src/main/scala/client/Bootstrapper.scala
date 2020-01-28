package client

import client.events.RenderHandler
import client.module.Operations.{LocalOperation, Operation, OperationRegistry, ServerOperation}
import client.util.ClientLogging
import com.typesafe.scalalogging.LazyLogging
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
  moduleLifetimeHooks: ModuleLifetimeHooks // Expand only as more hooks are needed
) extends LazyLogging {

  import ClientLogging._

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

  private def tryAuthenticate(): Unit = client.sendCommandLogged(Authenticate("artamus"))

  @tailrec
  private def sendCommands(commands: List[Command]): Unit = commands match {
    case Nil =>
    case command :: rest =>
      client.sendCommandLogged(command) match {
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
