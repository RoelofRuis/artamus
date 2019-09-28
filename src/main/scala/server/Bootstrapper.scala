package server

import javax.inject.Inject
import protocol.ServerInterface
import server.rendering.LilypondRenderingService

class Bootstrapper @Inject() (
  server: ServerInterface,
  rendering: LilypondRenderingService
) {

  def run(): Unit = {
    println("Starting server...")

    server.accept()
    rendering.shutdown()

    println("\nProgram ended")
  }
}
