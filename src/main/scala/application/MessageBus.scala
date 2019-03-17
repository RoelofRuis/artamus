package application

import application.command.Command
import application.component.ServiceRegistry
import application.controller.Controller
import application.ports.Logger
import javax.inject.Inject

import scala.collection.immutable
import scala.util.{Failure, Success, Try}

class MessageBus @Inject() private (
  controllers: immutable.Set[Controller],
  logger: ServiceRegistry[Logger]
) {

  def execute[Res, A](command: Command[Res], callback: Try[Res] => A): A = {
    logger.useAllActive(_.debug(s"Message bus received [$command]"))

    val response = controllers
      .map(_.handle(command))
      .collectFirst { case Some(r) => Success(r) }
      .getOrElse(Failure(new RuntimeException(s"No message handler found for [${command.getClass.getName}]")))

    callback(response)
  }

}
