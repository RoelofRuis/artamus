package protocol.transport.server

import java.io.{EOFException, IOException, ObjectInputStream, ObjectOutputStream}
import java.net.Socket

import com.typesafe.scalalogging.LazyLogging

import scala.util.{Failure, Success, Try}

private[server] class ServerConnectionFactory(server: ServerAPI) extends LazyLogging {

  def connect(socket: Socket, connection: Connection): Try[Runnable] = {
    try {
      lazy val objectIn = new ObjectInputStream(socket.getInputStream)
      val objectOut = new ObjectOutputStream(socket.getOutputStream)

      Success(new Runnable {
        override def run(): Unit = {
          server.connectionAccepted(connection, event => objectOut.writeObject(event))

          try {
            while (socket.isConnected) {
              val request = objectIn.readObject()

              val response = server.handleRequest(connection, request)

              objectOut.writeObject(response)
            }
          } catch {
            case _: EOFException => logger.info("EOF: Client hang up")
            case ex: IOException => logger.error("Connection thread encountered IOException", ex)
          } finally {
            server.connectionDropped(connection)
            socket.close()
          }
        }
      })
    } catch {
      case ex: Exception => Failure(ex)
    }
  }
}
