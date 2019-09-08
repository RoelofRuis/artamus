package protocol2

import java.net.{InetAddress, ServerSocket}

import protocol2.resource.ResourceManager

import scala.util.{Failure, Success}

object Client2Test extends App {

  case class X(s: String) { type Res = Boolean }

  val serverThread = new Thread(() => {
    val server = new ServerSocket(9999)
    val serverConnectionResource = new ResourceManager[ServerConnection](new ServerConnectionFactory(server))

    while ( ! serverConnectionResource.isClosed) {
      println("accept new!")

      serverConnectionResource.get match {
        case Success(conn) =>
          while (! conn.isClosed) {
            conn.receive[X] match {
              case Right(x) => println(s"Receiving [$x]")
              case Left(err) => println(s"Cannot receive [$err]")
            }
          }
          serverConnectionResource.discard

        case Failure(ex) =>
          println(s"Failed to accept connection [$ex]")
          serverConnectionResource.discard
      }
    }

    server.close()
  })

  serverThread.start()

  val client1 = SimpleObjectSocket(InetAddress.getByName("localhost"), 9999)

  println("sending: " + client1.send(X("question")))

  client1.close

  println("sending: " + client1.send(X("question 2")))

  val client2 = SimpleObjectSocket(InetAddress.getByName("localhost"), 9999)
  println("sending: " + client2.send(X("question 3")))
  println("sending: " + client2.send(X("question 4")))

  client2.close

  serverThread.join()

}
