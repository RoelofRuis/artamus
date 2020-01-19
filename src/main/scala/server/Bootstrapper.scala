package server

import javax.inject.Inject
import protocol.server.api.ServerFactory

class Bootstrapper @Inject() (
  serverFactory: ServerFactory
) {

  def run(): Unit = {
    serverFactory.create() match {
      case Right(server) => server.accept()
      case Left(ex) => println(s"Unable to create server: [$ex]")
    }

    println("\nProgram ended")
  }
}
