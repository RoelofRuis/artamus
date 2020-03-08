package network.server.impl

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.Socket

import network.DataResponseMessage
import network.Exceptions.InvalidMessage
import network.server.api.ServerAPI

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
            api.receiveFailed(CONNECTION, ex)
            Left(InvalidMessage)

          case Success(req) => api.handleRequest(CONNECTION, req)
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
