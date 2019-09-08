package protocol2

import java.net.ServerSocket

import protocol2.resource.ResourceManager

import scala.util.{Failure, Success}

class Server[A](port: Int) {

  val serverManager = new ResourceManager[ServerSocket](new ServerSocketFactory(port))
  val connectionManager = new ResourceManager[ServerConnection](new ServerConnectionFactory(serverManager))

  def accept(): Unit = {
    while (! connectionManager.isClosed) {
      println("Accepting new connection !")

      connectionManager.get match {
        case Success(conn) => receive(conn)
        case Failure(ex) =>
          println(s"Failed to accept connection [$ex]")
      }

      connectionManager.discard
    }

    serverManager.close
  }

  private def receive(conn: ServerConnection): Unit = {
    while (! conn.isClosed) {
      conn.receive[A] match {
        case Right(x) => println(s"Received [$x]")
        case Left(ex) => println(s"Error during receiving [$ex]")
      }
    }
  }

  def close: Iterable[Throwable] = connectionManager.close

}

