package protocol.server

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.Socket

import javax.inject.Inject
import protocol.Event

import scala.util.{Failure, Success, Try}

class ServerConnectionFactory @Inject() (bindings: ServerBindings) {

  private var connectionId: Long = 0
  private val SERVER_SUB_KEY = "server-out"

  def connect(socket: Socket): Try[Runnable] = {
    try {
      lazy val objectIn = new ObjectInputStream(socket.getInputStream)
      val objectOut = new ObjectOutputStream(socket.getOutputStream)

      val connection = createConnection(objectIn, objectOut, socket)

      Success(connectionRunnable(connection))
    } catch {
      case ex: Exception => Failure(ex)
    }
  }

  private def createConnection(
    objectIn: ObjectInputStream,
    objectOut: ObjectOutputStream,
    socket: Socket
  ): ServerConnection = {
    val in = new ServerInputStream(objectIn)
    val out = new ServerOutputStream(objectOut)

    new ServerConnection {
      def sendEvent[A <: Event](message: A): Unit = out.sendEvent(message)

      def handleNext(): Unit = {
        in.readNext(bindings) match {
          case Right(response) => out.sendData(response)
          case Left(error) => out.sendError(error)
        }
      }

      override def isOpen: Boolean = socket.isConnected

      override def close(): Unit = {
        objectIn.close()
        objectOut.close()
        socket.close()
      }
    }
  }

  private def connectionRunnable(connection: ServerConnection): Runnable = {
    val connectionId = nextConnectionId
    () => {
      bindings.eventSubscriber.subscribe(connectionId, connection.sendEvent)

      while (connection.isOpen) {
        connection.handleNext()
      }

      bindings.eventSubscriber.unsubscribe(connectionId)

      connection.close()
    }
  }

  /* @NotThreadSafe */
  private def nextConnectionId: String = {
    val subId = s"${SERVER_SUB_KEY}_$connectionId"
    connectionId+=1
    subId
  }

}


