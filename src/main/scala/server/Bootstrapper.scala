package server

import javax.inject.Inject
import protocol.{Server, ServerBindings}
import server.handler.{CommandHandler, ControlHandler}

class Bootstrapper @Inject() (
  server: Server,
  commandHandler: CommandHandler,
  controlHandler: ControlHandler,
) extends App {

  def run(): Unit = {
    val serverThread = new Thread(() =>
      server.acceptConnections(ServerBindings(commandHandler.execute, controlHandler.handle))
    )

    println("Starting server...")

    serverThread.start()

    println("Accepting commands on command bus...")

    serverThread.join()

    println("Server shut down")
  }
}
