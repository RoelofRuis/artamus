package protocol2

import java.net.InetAddress

import protocol2.server.{MessageHandler, Server}

object Client2Test extends App {

  case class X(s: String) { type Res = Boolean }
  case class Y(s: Double) { type Res = Boolean }

  class Handler[A] extends MessageHandler {
    override def handle(msg: Object): Either[Throwable, Any] = {
      val x = msg.asInstanceOf[A]
      x match {
        case msg: X => Right(msg)
        case _ => Left(new Exception("Kutzooi!"))
      }
    }
  }

  val handler = new Handler[X]

  val port = 9999
  val server = new Server(port, handler)

  val serverThread = new Thread(() => server.accept())

  serverThread.start()

  val client1 = SimpleObjectSocket(InetAddress.getByName("localhost"), port)

  client1.send(X("question"))
  client1.send(Y(42.0))

  Thread.sleep(2000)

  server.close
  serverThread.join()

}
