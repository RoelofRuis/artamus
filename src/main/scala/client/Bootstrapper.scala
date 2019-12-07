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

      sendCommands(op())

      if (input == "quit") isRunning = false
    }

    renderHandler.frame.dispose()
    client.close()
  }

  def sendCommands(commands: List[Command]): Unit = commands match {
    case Nil =>
    case command :: rest => client.sendCommand(command) match {
      case Some(res) => println(s"[$command] -> [$res]")
        sendCommands(rest)
      case None => println(s"[$command] -> failed! (skipping [${rest.length}] more)")
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
