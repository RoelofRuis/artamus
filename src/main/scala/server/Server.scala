package server

import javax.inject.Inject
import server.io.{CommandSocket, Logger}

class Server @Inject() (logger: Logger, commandBus: CommandSocket) extends App {

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
