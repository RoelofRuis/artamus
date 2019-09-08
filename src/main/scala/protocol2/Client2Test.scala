package protocol2

import java.net.InetAddress

object Client2Test extends App {

  case class X(s: String) { type Res = Boolean }
  val port = 9999
  val server = new Server[X](port)

  val serverThread = new Thread(() => server.accept())

  serverThread.start()
  server.close

  val client1 = SimpleObjectSocket(InetAddress.getByName("localhost"), port)

  println("sending: " + client1.send(X("question")))

  client1.close

  println("sending: " + client1.send(X("question 2")))

  val client2 = SimpleObjectSocket(InetAddress.getByName("localhost"), port)
  println("sending: " + client2.send(X("question 3")))
  println("sending: " + client2.send(X("question 4")))

  client2.close

  serverThread.join()

}
