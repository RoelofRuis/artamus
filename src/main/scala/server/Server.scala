package server

import javax.inject.Inject
import server.io.CommandSocket
import util.Logger

class Server @Inject() (logger: Logger, commandSocket: CommandSocket) extends App {

  def run(): Unit = {
    val serverThread = new Thread(() => commandSocket.run())

    logger.debug("Starting server...")

    serverThread.start()

    logger.debug("Accepting commands on command bus...")

    serverThread.join()

    logger.debug("Server shut down")
  }
}
