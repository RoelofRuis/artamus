package protocol2

import java.net.{InetAddress, ServerSocket}

import protocol2.resource.ResourceManager

object Client2Test extends App {

  case class X(s: String) { type Res = Boolean }

  val serverThread = new Thread(() => {
    val ss = new ServerSocket(9999)
    println("before accept")
    val conn = ss.accept()

    val s = new SimpleObjectSocket(
      new ResourceManager[ObjectSocketConnection](
        new ServerObjectSocketFactory(conn)
      )
    )

    println("receiving" + s.receive[X])
  })

  serverThread.start()

  val c = new SimpleObjectSocket(
    new ResourceManager[ObjectSocketConnection](
      new ClientObjectSocketFactory(
        InetAddress.getByName("localhost"),
        9999
      )
    )
  )

  println("sending" + c.send(X("question")))

  serverThread.join()

}
