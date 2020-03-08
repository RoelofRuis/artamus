package client

import client.infra.Client
import client.module.Operations.{LocalOperation, OperationRegistry, ServerOperation}
import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject

import scala.util.{Failure, Success}

class CommandExecutor @Inject() (
  client: Client,
  registry: OperationRegistry,
) extends LazyLogging {

  import _root_.client.infra.ClientInteraction._

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
          case Success(commands) => client.sendCommandList(commands)
          case Failure(ex) => logger.error("Error when fetching operations", ex)
        }
        true
    }
  }

}
