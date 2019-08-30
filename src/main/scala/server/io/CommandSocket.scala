package server.io

import java.net.{ServerSocket, SocketException}

import javax.inject.Inject
import protocol._
import server.api.Server.Disconnect

private[server] class CommandSocket @Inject() private (
  commandHandler: CommandHandler,
  eventBus: EventBus,
  logger: Logger
) {

  private lazy val server = new ServerSocket(9999)

  def run(): Unit = {
    var acceptNewConnections = true

    while (acceptNewConnections) {
      try {
        // TODO: clean up with better separation of concerns, then move to protocol

        // Accept connection
        val socket = server.accept()
        val connection = protocol.serverConnection(socket)
        var connectionOpen = true

        val clientToken = "client"

        eventBus.subscribe(clientToken, { event => connection.sendEvent(event) })

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

        // Receive messages
        while (connectionOpen) {
          connection.handleNext(commandHandler.execute, executeControlMessage)
        }

        // Clean up
        eventBus.unsubscribe(clientToken)
        socket.close()
      } catch {
        case ex: SocketException => logger.debug(s"Socket Exception [$ex]")
          ex.printStackTrace()
      }
    }
  }
}
