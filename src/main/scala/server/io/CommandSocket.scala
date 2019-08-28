package server.io

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.{ServerSocket, SocketException}

import javax.inject.Inject
import server.api.commands.Command

private[server] class CommandSocket @Inject() private (
  commandHandler: CommandHandler,
  logger: Logger
) {

  lazy val server = new ServerSocket(9999)

  def run(): Unit = {
    try {
      while (! Thread.interrupted()) {
        val socket = server.accept()
        val input = new ObjectInputStream(socket.getInputStream)

        val command = input.readObject().asInstanceOf[Command]

        logger.io("SOCKET COMMAND", "IN", s"$command")

        val response = commandHandler.execute(command)

        logger.io("SOCKET COMMAND", "OUT", s"$response")

        val output = new ObjectOutputStream(socket.getOutputStream)

        output.writeObject(response)

        socket.close()
      }
    } catch {
      case _: SocketException => logger.debug("Socked closed unexpectedly")
    }
  }

  def close(): Unit = {
    server.close()
  }
}
