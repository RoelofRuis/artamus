package protocol.server

import java.io.{IOException, ObjectInputStream, ObjectOutputStream}
import java.net.Socket

import javax.inject.Inject

import scala.util.{Failure, Success, Try}

class ServerConnectionFactory @Inject() (bindings: ServerBindings) {

  private var connectionId: Long = 0
  private val SERVER_SUB_KEY = "server-out"

  def connect(socket: Socket): Try[Runnable] = {
    try {
      lazy val objectIn = new ObjectInputStream(socket.getInputStream)
      val objectOut = new ObjectOutputStream(socket.getOutputStream)

      val connectionId = nextConnectionId

      Success(new Runnable {
        override def run(): Unit = {
          bindings.subscribe(connectionId)

          try {
            while (socket.isConnected) {
              val request = objectIn.readObject()
              val payload = objectIn.readObject()

              bindings.handleRequest(request, payload).foreach(objectOut.writeObject)
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

  /* @NotThreadSafe */
  private def nextConnectionId: String = {
    val subId = s"${SERVER_SUB_KEY}_$connectionId"
    connectionId+=1
    subId
  }

}


