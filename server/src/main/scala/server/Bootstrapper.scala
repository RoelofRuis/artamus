package server

import api.{Event, Request}
import javax.inject.Inject
import protocol.server.api.ServerFactory

class Bootstrapper @Inject() (
  serverFactory: ServerFactory[Request, Event]
) {

  def run(): Unit = {
    serverFactory.create() match {
      case Right(server) => server.accept()
      case Left(ex) => println(s"Unable to create server: [$ex]")
    }

    println("\nProgram ended")
  }
}
