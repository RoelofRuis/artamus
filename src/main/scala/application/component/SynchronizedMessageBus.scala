package application.component

import java.util.concurrent.SynchronousQueue

import application.command.ApplicationCommand.CloseApplication
import application.command.Command
import application.handler.CommandHandler
import application.ports.MessageBus
import javax.inject.Inject

import scala.collection.immutable
import scala.util.{Failure, Try}

private[application] class SynchronizedMessageBus @Inject() private (
  handlers: immutable.Set[CommandHandler],
  logger: Logger
) extends MessageBus {

  private val queueIn: SynchronousQueue[Command[_]] = new SynchronousQueue[Command[_]]()
  private val queueOut: SynchronousQueue[Try[_]] = new SynchronousQueue[Try[_]]()

  def execute[Res](command: Command[Res]): Try[Res] = synchronized {
    logger.io("MESSAGE BUS", "EXE", s"$command")

    queueIn.put(command)
    val res = queueOut.take().flatMap(res => Try(res.asInstanceOf[Res]))

    logger.io("MESSAGE BUS", "RES",s"$res")

    res
  }

  def handle[Res](): Boolean = {
    val command = queueIn.take().asInstanceOf[Command[Res]]

    val res: Try[Res] = handlers
      .map(_.handle[Res])
      .reduceLeft(_ orElse _)
      .lift(command)
      .getOrElse(Failure(new RuntimeException(s"No message handler found for [${command.getClass.getName}]")))

    queueOut.add(res)

    ! command.isInstanceOf[CloseApplication.type]
  }

}
