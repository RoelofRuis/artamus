package server

import javax.inject.Inject
import protocol.server._
import server.rendering.LilypondRenderingService

class Bootstrapper @Inject() (
  server: SimpleServer,
  rendering: LilypondRenderingService
) {

  def run(): Unit = {
    println("Starting server...")

    server.accept()
    rendering.shutdown()

    println("Program ended")
  }
}
