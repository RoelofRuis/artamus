package protocol.server.impl

import java.io.{EOFException, IOException, ObjectInputStream, ObjectOutputStream}
import java.net.Socket

import protocol.server.api.ServerAPI

class Connection(
  api: ServerAPI,
  socket: Socket,
  inputStream: ObjectInputStream,
  outputStream: ObjectOutputStream
) extends Runnable {

  final private val CONNECTION = ConnectionHandleImpl(outputStream)

  override def run(): Unit = {
    api.connectionOpened(CONNECTION)

    try {
      while (socket.isConnected) {
        val request = inputStream.readObject()

        val mainResponse = api.handleRequest(CONNECTION, request)
        val afterRequestResponse = api.afterRequest(CONNECTION, mainResponse)

        outputStream.writeObject(afterRequestResponse)
      }
    } catch {
      case _: EOFException => // shit is fokt!
      case ex: IOException => // wat nu?
    } finally {
      api.connectionClosed(CONNECTION)
      socket.close()
    }
  }

}
