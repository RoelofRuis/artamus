package application

import application.interact.{ApplicationEventBus, Logger, SocketCommandBus}
import javax.inject.Inject

private[application] class Application @Inject() private (
  commandBus: SocketCommandBus,
  eventBus: ApplicationEventBus,
  logger: Logger
) extends ApplicationEntryPoint {

  def run(): Unit = {
    logger.debug("Starting server...")

    val commandBusThread = new Thread(() => commandBus.run())

    logger.debug(s"Running command bus ...")

    commandBusThread.start()

    logger.debug("Accepting commands on command bus...")

    commandBusThread.join()

    logger.debug("Shutting down, bye!")
  }

}

