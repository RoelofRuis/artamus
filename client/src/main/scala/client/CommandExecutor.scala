package client

import client.infra.Client
import client.module.Operations.{LocalOperation, OperationRegistry, ServerOperation}
import com.typesafe.scalalogging.LazyLogging
import domain.interact.Command
import javax.inject.Inject

import scala.annotation.tailrec
import scala.util.{Failure, Success}

class CommandExecutor @Inject() (
  client: Client,
  registry: OperationRegistry,
) extends LazyLogging {

  def execute(input: String): Boolean = {
    registry.getOperation(input) match {
      case None => false
      case Some(op: LocalOperation) =>
        op.f() match {
          case Success(()) =>
          case Failure(ex) => logger.error("Error when fetching operations", ex)
        }
        true

      case Some(op: ServerOperation) =>
        op.f() match {
          case Success(commands) => sendCommands(commands)
          case Failure(ex) => logger.error("Error when fetching operations", ex)
        }
        true
    }
  }

  @tailrec
  private def sendCommands(commands: List[Command]): Unit = commands match {
    case Nil =>
    case command :: rest =>
      client.send(command) match {
        case Right(_) =>
          println(s"[$command] executed")
          sendCommands(rest)
        case Left(ex) =>
          println(s"[$command] failed")
          println(s"cause [${ex.name}: ${ex.description}]")
          ex.cause.foreach(_.printStackTrace)
          println(s"skipping [${rest.length}] more")
      }
  }

}
