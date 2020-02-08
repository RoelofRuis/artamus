package server

import domain.interact.{Event, Request}
import javax.inject.Inject
import network.server.api.ServerFactory

class Bootstrapper @Inject() (
  serverFactory: ServerFactory[Request, Event]
) {

  import scala.concurrent.ExecutionContext.Implicits.global

  def run(): Unit = {
    serverFactory.create() match {
      case Right(server) =>
        server.accept()
        server.awaitShutdown().foreach { _ => println("\nProgram ended") }
      case Left(ex) => println(s"Unable to create server: [$ex]")
    }

  }
}
