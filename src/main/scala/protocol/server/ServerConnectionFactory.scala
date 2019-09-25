package protocol.server

import java.io.{ObjectInputStream, ObjectOutputStream}
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

      val in = new ServerInputStream(objectIn) // TODO: this logic has to be pushed outwards
      val out = new ServerOutputStream(objectOut) // TODO: this logic has to be pushed outwards

      val connectionId = nextConnectionId

      Success(new Runnable {
        override def run(): Unit = {
          bindings.eventSubscriber.subscribe(connectionId, message => out.sendEvent(message))

          try {
            while (socket.isConnected) {
              in.readNext(bindings) match {
                case Right(response) => out.sendData(response)
                case Left(error) => out.sendError(error)
              }
            }
          } catch {
            case ex: InterruptedException =>
              println(s"Connection was interrupted [$ex]")

            case ex: Throwable =>
              ex.printStackTrace()
          } finally {
            bindings.eventSubscriber.unsubscribe(connectionId)
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


