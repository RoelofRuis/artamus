package protocol.client2

import java.net.InetAddress

object Client2Test extends App {

  case class X(s: String) { type Res = Boolean }

  val c = new SimpleClient(
    new ConnectionManager(
      new SimpleConnectionFactory(
        InetAddress.getByName("localhost"),
        9999
      )
    )
  )

  c.send(X("jong!"))

}
