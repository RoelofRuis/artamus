package protocol2

import java.net.{InetAddress, ServerSocket}

object Client2Test extends App {

  case class X(s: String) { type Res = Boolean }

  val serverThread = new Thread(() => {
    val ss = new ServerSocket(9999)

    val serverConnection = new ServerConnection(ss.accept())

    var connOpen = true

    while(connOpen) {
      serverConnection.receive[X] match {
        case Right(x) => println(s"Receiving [$x]")
        case Left(err) =>
          println(s"Cannot receive [$err]")
          connOpen = false
      }
    }

    ss.close()
  })

  serverThread.start()

  val client = new ClientConnection(InetAddress.getByName("localhost"), 9999)

  println("sending: " + client.send(X("question")))

  client.close

  println("sending: " + client.send(X("question 2")))

  serverThread.join()

}
