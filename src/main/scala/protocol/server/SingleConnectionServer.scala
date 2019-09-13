package protocol.server

import java.net.{ServerSocket, SocketException}

private[protocol] class SingleConnectionServer private[protocol](port: Int) extends ServerInterface {

  private lazy val server = new ServerSocket(port)

  private var isServerRunning = true
  private var isConnectionOpen = false

  private val SERVER_SUB_KEY = "server-out"

  def acceptConnections(bindings: ServerBindings): Unit = {
    while(isServerRunning) {
      try {
        val socket = server.accept()
        val connection = new ServerConnection(socket)
        isConnectionOpen = true

        bindings.eventSubscriber.subscribe(SERVER_SUB_KEY, connection.sendEvent)

        while (isConnectionOpen) {
          connection.handleNext(bindings)
        }

        bindings.eventSubscriber.unsubscribe(SERVER_SUB_KEY)
        connection.close()
        socket.close()
      } catch {
        case ex: SocketException =>
          ex.printStackTrace()
          stopServer()
      }
    }

    server.close()
  }

  def closeActiveConnection(): Unit = isConnectionOpen = false

  def stopServer(): Unit = {
    closeActiveConnection()
    isServerRunning = false
  }

}
