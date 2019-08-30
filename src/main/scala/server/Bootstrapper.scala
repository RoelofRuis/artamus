package server

import javax.inject.Inject
import protocol.ServerInterface
import protocol.ServerInterface.ServerBindings
import server.handler.{CommandDispatcherImpl, ControlDispatcherImpl, QueryDispatcherImpl}

class Bootstrapper @Inject() (
  server: ServerInterface,
  commandDispatcher: CommandDispatcherImpl,
  controlDispatcher: ControlDispatcherImpl,
  queryDispatcher: QueryDispatcherImpl,
) extends App {

  def run(): Unit = {
    val serverThread = new Thread(() =>
      server.acceptConnections(
        ServerBindings(
          commandDispatcher,
          controlDispatcher,
          queryDispatcher
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
