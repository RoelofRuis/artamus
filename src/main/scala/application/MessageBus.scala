package application

import application.command.Command
import application.component.ServiceRegistry
import application.controller.Controller
import application.ports.Logger
import javax.inject.Inject

import scala.collection.immutable
import scala.util.{Failure, Try}

class MessageBus @Inject() private (
  controllers: immutable.Set[Controller],
  logger: ServiceRegistry[Logger]
) {

  def execute[Res](command: Command[Res]): Try[Res] = {
    logger.useAllActive(_.debug(s"Message Bus received [$command]"))

    controllers
      .map(_.handle[Res])
      .reduceLeft(_ orElse _)
      .lift(command)
      .getOrElse(Failure(new RuntimeException(s"No message handler found for [${command.getClass.getName}]")))
  }

}
