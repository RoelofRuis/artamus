package server

import javax.inject.Inject
import protocol.server._

class Bootstrapper @Inject() (
  server: SimpleServer
) {

  def run(): Unit = {
    println("Starting server...")

    server.accept()

    println("Program ended")
  }
}
