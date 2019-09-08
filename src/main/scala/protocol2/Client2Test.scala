package protocol2

import java.net.InetAddress

object Client2Test extends App {

  case class X(s: String) { type Res = Boolean }

  val c = new SimpleObjectSocket(
    new ConnectionManager(
      new ClientObjectSocketFactory(
        InetAddress.getByName("localhost"),
        9999
      )
    )
  )

  c.send(X("jong!"))

}
