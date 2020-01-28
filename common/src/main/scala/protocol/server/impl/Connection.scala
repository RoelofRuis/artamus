package protocol.server.impl

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.Socket

import protocol.server.api.ServerAPI

private[server] final class Connection(
  api: ServerAPI,
  socket: Socket,
  inputStream: ObjectInputStream,
  outputStream: ObjectOutputStream
) extends Runnable {

  final private val CONNECTION = ConnectionHandleImpl(outputStream)

  override def run(): Unit = {
    api.connectionOpened(CONNECTION)

    try {
      while (! socket.isClosed) {
        val request = inputStream.readObject()

        val mainResponse = api.handleRequest(CONNECTION, request)
        val afterRequestResponse = api.afterRequest(CONNECTION, mainResponse)

        outputStream.writeObject(afterRequestResponse)
      }
      api.connectionClosed(CONNECTION, None)
    } catch {
      case ex: Throwable => api.connectionClosed(CONNECTION, Some(ex))
    } finally {
      socket.close()
    }
  }

}
