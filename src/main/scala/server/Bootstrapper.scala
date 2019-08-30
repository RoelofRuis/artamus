package server

import javax.inject.Inject
import protocol.{Server, ServerBindings}
import server.handler.{CommandDispatcherImpl, ControlDispatcherImpl, QueryDispatcherImpl}

class Bootstrapper @Inject() (
  server: Server,
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
