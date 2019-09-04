package server

import javax.inject.Inject
import protocol._
import protocol.server.{ServerBindings, ServerInterface}

class Bootstrapper @Inject() (
  server: ServerInterface,
  commandDispatcher: Dispatcher[Command],
  controlDispatcher: Dispatcher[Control],
  queryDispatcher: Dispatcher[Query],
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
