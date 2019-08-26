import java.io.PrintStream
import java.net.{InetAddress, Socket}

import scala.io.BufferedSource

object Client extends App {

  val s = new Socket(InetAddress.getByName("localhost"), 9999)
  lazy val in = new BufferedSource(s.getInputStream).getLines()
  val out = new PrintStream(s.getOutputStream)

  out.println("Hello, world")
  out.flush()
  println("Received: " + in.next)

  s.close()

}
