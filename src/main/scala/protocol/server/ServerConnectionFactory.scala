package protocol.server

import java.io.{IOException, ObjectInputStream, ObjectOutputStream}
import java.net.Socket

import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import protocol.ServerBindings

import scala.util.{Failure, Success, Try}

class ServerConnectionFactory @Inject() (bindings: ServerBindings) extends LazyLogging {

  def connect(socket: Socket, connectionId: String): Try[Runnable] = {
    try {
      lazy val objectIn = new ObjectInputStream(socket.getInputStream)
      val objectOut = new ObjectOutputStream(socket.getOutputStream)

      Success(new Runnable {
        override def run(): Unit = {
          bindings.subscribe(connectionId)

          try {
            while (socket.isConnected) {
              val request = objectIn.readObject()
              logger.debug(s"Read request [$request]")

              val response = bindings.handleRequest(request)
              logger.debug(s"Sending response [$response]")
              objectOut.writeObject(response)
            }
          } catch {
            case ex: IOException => println(s"Connection thread encountered IOException [$ex]")

            case ex: InterruptedException => println(s"Connection thread was interrupted [$ex]")

            case ex: Exception => println(s"Exception in connection thread [$ex]")
          } finally {
            bindings.unsubscribe(connectionId)
            socket.close()
          }
        }
      })
    } catch {
      case ex: Exception => Failure(ex)
    }
  }
}


