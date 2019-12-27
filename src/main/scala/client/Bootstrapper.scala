package client

import client.events.RenderHandler
import client.operations.{Operation, OperationRegistry}
import javax.inject.Inject
import protocol.Command
import protocol.client.api.ClientInterface2

class Bootstrapper @Inject() (
  client: ClientInterface2,
  registry: OperationRegistry,
  renderHandler: RenderHandler
) {

  def run(): Unit = {
    var isRunning = true

    while(isRunning) {
      val (input, op) = nextOperation

      sendCommands(op())

      if (input == "quit") isRunning = false
    }

    renderHandler.frame.dispose()
  }

  def sendCommands(commands: List[Command]): Unit = commands match {
    case Nil =>
    case command :: rest =>
      client.sendCommand(command) match {
        case None =>
          println(s"[$command] executed")
          sendCommands(rest)
        case Some(ex) =>
          println(s"[$command] -> failed! (skipping [${rest.length}] more)")
          ex.printStackTrace()
      }
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
