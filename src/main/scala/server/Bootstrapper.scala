package server

import javax.inject.Inject
import protocol.ServerInterface
import server.rendering.Renderer

class Bootstrapper @Inject() (
  server: ServerInterface,
  renderer: Renderer
) {

  def run(): Unit = {
    println("Starting server...")

    server.accept()
    renderer.shutdown()

    println("\nProgram ended")
  }
}
