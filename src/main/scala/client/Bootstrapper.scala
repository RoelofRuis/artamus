package client

import client.operations.{ClientOperationRegistry, Operation, Quit}
import javax.inject.Inject
import protocol.client.ClientInterface

class Bootstrapper @Inject() (
  client: ClientInterface,
  registry: ClientOperationRegistry
) {

  def nextOperation: Operation = {
    println("Input next command:")
    val input = scala.io.StdIn.readLine()
    registry.getOperation(input) match {
      case Some(op) => op
      case None => nextOperation
    }
  }

  def run(): Unit = {
    var isRunning = true

    // TODO: refine much further
    while(isRunning) {
      // TODO: wrap in controll thread
      val op = nextOperation

      op
        .getControl
        .map { control => (control, client.sendControl(control)) }
        .foreach { case (control, res) => println(s"$control -> $res") }

      op
        .getCommands
        .map { command => (command, client.sendCommand(command)) }
        .foreach { case (command, res) => println(s"$command -> $res") }

      if (op.isInstanceOf[Quit.type]) isRunning = false
    }

    client.close()
  }

}
