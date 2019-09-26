package client

import client.operations.{Operation, OperationRegistry}
import javax.inject.Inject
import protocol.Command
import protocol.client.ClientInterface

class Bootstrapper @Inject() (
  client: ClientInterface,
  registry: OperationRegistry
) {

  def run(): Unit = {
    var isRunning = true

    while(isRunning) {
      val (input, op) = nextOperation

      op().foreach {
        case command: Command => client.sendCommand(command).foreach(res => println(s"$command -> $res"))
        case _ =>
      }

      if (input == "quit") isRunning = false
    }

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
