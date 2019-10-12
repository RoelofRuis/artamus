package client

import client.events.RenderHandler
import client.operations.{Operation, OperationRegistry}
import javax.inject.Inject
import protocol.{ClientInterface, Command}

class Bootstrapper @Inject() (
  client: ClientInterface,
  registry: OperationRegistry,
  renderHandler: RenderHandler
) {

  def run(): Unit = {
    client.open()
    var isRunning = true

    while(isRunning) {
      val (input, op) = nextOperation

      op().foreach {
        case command: Command => client.sendCommand(command).foreach(res => println(s"$command -> $res"))
        case _ =>
      }

      if (input == "quit") isRunning = false
    }

    renderHandler.frame.dispose()
    client.close()
  }

  def nextOperation: (String, Operation) = {
    println("Input next command:")
    val input = scala.io.StdIn.readLine()
    registry.getOperation(input) match {
      case Some(op) => (input, op)
      case None =>
        println(s"Unknown command [$input]")
        nextOperation
    }
  }

}
