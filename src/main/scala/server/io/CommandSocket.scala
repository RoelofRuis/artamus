package server.io

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.{ServerSocket, SocketException}

import javax.inject.Inject
import server.api.messages.{Command, IncomingMessageType}
import util.{Logger, SafeObjectInputStream}

import scala.util.Failure

private[server] class CommandSocket @Inject() private (
  commandHandler: CommandHandler,
  logger: Logger
) {

  private lazy val server = new ServerSocket(9999)

  def run(): Unit = {
    while (! server.isClosed) {
      try {
        val socket = server.accept()

        val input = new SafeObjectInputStream(new ObjectInputStream(socket.getInputStream), Some(logger))
        val output = new ObjectOutputStream(socket.getOutputStream)

        val response = input.readObject[IncomingMessageType]()
          .flatMap(_ => input.readObject[Command]().map(commandHandler.execute))
          .transform(identity, _ => Failure(InvalidRequestException(s"Received invalid message")))

        output.writeObject(response)

        socket.close()
      } catch {
        case ex: SocketException => if (! server.isClosed) logger.debug(s"Socket Exception [$ex]")
      }
    }
  }

  def close(): Unit = {
    server.close()
  }
}
