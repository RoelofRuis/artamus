package client

import client.events.RenderHandler
import client.operations.{Operation, OperationRegistry}
import javax.inject.Inject
import midi.v2.MidiDeviceLoader
import protocol.Command
import protocol.client.api.ClientInterface
import server.actions.control.Authenticate

import scala.util.{Failure, Success}

class Bootstrapper @Inject() (
  client: ClientInterface,
  registry: OperationRegistry,
  renderHandler: RenderHandler,
  midiDeviceLoader: MidiDeviceLoader
) {

  def run(): Unit = {
    var isRunning = true

    tryAuthenticate()

    while (isRunning) {
      val (input, op) = nextOperation

      op() match {
        case Success(commands) => sendCommands(commands)
        case Failure(ex) =>
          println("Unable to execute command:")
          ex.printStackTrace()
      }

      if (input == "quit") isRunning = false
    }

    midiDeviceLoader.closeAll()
    renderHandler.frame.dispose()
  }

  private def tryAuthenticate(): Unit = {
    client.sendCommand(Authenticate("artamus"))
  }

  private def sendCommands(commands: List[Command]): Unit = commands match {
    case Nil =>
    case command :: rest =>
      client.sendCommand(command) match {
        case None =>
          println(s"[$command] executed")
          sendCommands(rest)
        case Some(ex) =>
          println(s"[$command] failed")
          println(s"cause [${ex.name}: ${ex.description}]")
          ex.cause.foreach(_.printStackTrace)
          println(s"skipping [${rest.length}] more")
      }
  }

  private def nextOperation: (String, Operation) = {
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
