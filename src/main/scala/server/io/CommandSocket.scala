package server.io

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.{ServerSocket, SocketException}

import javax.inject.Inject
import server.api.messages._
import util.{Logger, SafeObjectInputStream}

import scala.util.{Failure, Success}

private[server] class CommandSocket @Inject() private (
  commandHandler: CommandHandler,
  logger: Logger
) {

  private lazy val server = new ServerSocket(9999)

  def run(): Unit = {
    var acceptNewConnections = true

    while (acceptNewConnections) {
      try {
        val socket = server.accept()
        var connectionOpen = true
        val input = new SafeObjectInputStream(new ObjectInputStream(socket.getInputStream), Some(logger))
        val output = new ObjectOutputStream(socket.getOutputStream)

        // TODO: make cleaner separation so it becomes easier to send callback events

        // TODO: separate out the control actions
        def executeControlMessage[A <: Control](msg: A): Boolean = {
          msg match {
            case Disconnect(false) =>
              connectionOpen = false

            case Disconnect(true) =>
              connectionOpen = false
              acceptNewConnections = false
          }
          true
        }

        while (connectionOpen) {
          val response = input.readObject[ServerRequestMessage]()
            .flatMap {
              case CommandMessage => input.readObject[Command]().map(commandHandler.execute)
              case ControlMessage => input.readObject[Control]().map(m => Success(executeControlMessage(m)))
            }
            .transform(identity, _ => Failure(InvalidRequestException(s"Received invalid message")))

          output.writeObject(ResponseMessage)
          output.writeObject(response)
        }

        socket.close()
      } catch {
        case ex: SocketException => logger.debug(s"Socket Exception [$ex]")
      }
    }
  }
}
