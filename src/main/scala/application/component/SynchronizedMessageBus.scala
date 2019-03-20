package application.component

import java.util.concurrent.SynchronousQueue

import application.command.ApplicationCommand.CloseApplication
import application.command.Command
import application.controller.Controller
import application.ports.{Logger, MessageBus}
import javax.inject.Inject

import scala.collection.immutable
import scala.util.{Failure, Try}

private[application] class SynchronizedMessageBus @Inject() private (
  controllers: immutable.Set[Controller],
  logger: Logger
) extends MessageBus {

  private val queueIn: SynchronousQueue[Command[_]] = new SynchronousQueue[Command[_]]()
  private val queueOut: SynchronousQueue[Try[_]] = new SynchronousQueue[Try[_]]()

  def execute[Res](command: Command[Res]): Try[Res] = synchronized {
    logger.debug(s"Message Bus received [$command]")

    queueIn.put(command)
    val res = queueOut.take().flatMap(res => Try(res.asInstanceOf[Res]))

    logger.debug(s"Handled with result [$res]")

    res
  }

  def handle[Res](): Boolean = {
    val command = queueIn.take().asInstanceOf[Command[Res]]

    val res: Try[Res] = controllers
      .map(_.handle[Res])
      .reduceLeft(_ orElse _)
      .lift(command)
      .getOrElse(Failure(new RuntimeException(s"No message handler found for [${command.getClass.getName}]")))

    queueOut.add(res)

    ! command.isInstanceOf[CloseApplication.type]
  }

}
