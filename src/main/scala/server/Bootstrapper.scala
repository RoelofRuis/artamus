package server

import javax.inject.Inject
import protocol.{Server, ServerBindings}
import server.handler.{CommandHandlerImpl, ControlHandlerImpl, QueryHandlerImpl}

class Bootstrapper @Inject() (
  server: Server,
  commandHandler: CommandHandlerImpl,
  controlHandler: ControlHandlerImpl,
  queryHandler: QueryHandlerImpl,
) extends App {

  def run(): Unit = {
    val serverThread = new Thread(() =>
      server.acceptConnections(
        ServerBindings(
          commandHandler,
          controlHandler,
          queryHandler
        )
      )
    )

    println("Starting server...")

    serverThread.start()

    println("Accepting commands on command bus...")

    serverThread.join()

    println("Server shut down")
  }
}
