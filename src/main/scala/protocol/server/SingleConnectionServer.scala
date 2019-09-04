package protocol.server

import java.net.{ServerSocket, SocketException}

private[protocol] class SingleConnectionServer private[protocol](port: Int) extends ServerInterface {

  private lazy val server = new ServerSocket(port)
  private val eventRegistry = new ServerEventRegistry
  private val eventBus = new ServerEventBus(eventRegistry)

  private var isServerRunning = true
  private var isConnectionOpen = false

  def acceptConnections(bindings: ServerBindings): Unit = {
    while(isServerRunning) {
      try {
        val socket = server.accept()
        val connection = new ServerConnection(socket)
        isConnectionOpen = true

        eventRegistry.subscribe { connection.sendEvent }

        while (isConnectionOpen) {
          connection.handleNext(bindings)
        }

        eventRegistry.unsubscribe()
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

  override def getEventBus: ServerEventBus = eventBus

  def closeActiveConnection(): Unit = isConnectionOpen = false

  def stopServer(): Unit = {
    closeActiveConnection()
    isServerRunning = false
  }

}
