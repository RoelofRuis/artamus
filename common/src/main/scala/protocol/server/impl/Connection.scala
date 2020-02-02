package protocol.server.impl

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.Socket

import protocol.DataResponseMessage
import protocol.server.api.ServerAPI

import scala.util.{Failure, Success, Try}

private[server] final class Connection[R, E](
  api: ServerAPI[R, E],
  socket: Socket,
  inputStream: ObjectInputStream,
  outputStream: ObjectOutputStream
) extends Runnable {

  final private val CONNECTION = ConnectionHandleImpl[E](outputStream)

  override def run(): Unit = {
    api.connectionOpened(CONNECTION)

    try {
      while (! socket.isClosed) {
        val requestObject = inputStream.readObject()

        val response = Try { requestObject.asInstanceOf[R] } match {
          case Failure(ex) =>
            Left(api.handleReceiveFailure(CONNECTION, ex))

          case Success(req) =>
            val mainResponse = api.handleRequest(CONNECTION, req)
            api.afterRequest(CONNECTION, mainResponse)
        }

        outputStream.writeObject(DataResponseMessage(response))
      }
      api.connectionClosed(CONNECTION, None)
    } catch {
      case ex: Throwable => api.connectionClosed(CONNECTION, Some(ex))
    } finally {
      socket.close()
    }
  }

}
