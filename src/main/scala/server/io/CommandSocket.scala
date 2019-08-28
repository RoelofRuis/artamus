package server.io

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.{ServerSocket, SocketException}

import javax.inject.Inject
import server.api.commands.Command

import scala.util.{Failure, Try}

private[server] class CommandSocket @Inject() private (
  commandHandler: CommandHandler,
  logger: Logger
) {

  private lazy val server = new ServerSocket(9999)

  def run(): Unit = {
    while (! server.isClosed) {
      try {
        val socket = server.accept()

        val input = new ObjectInputStream(socket.getInputStream)
        val output = new ObjectOutputStream(socket.getOutputStream)

        val command = Try(input.readObject().asInstanceOf[Command])
        logger.io("SERVER SOCKET", "IN", s"$command")

        val response = command.fold(
          _ => Failure(InvalidRequestException(s"Cannot parse [$input]")),
          commandHandler.execute
        )

        logger.io("SERVER SOCKET", "OUT", s"$response")

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
