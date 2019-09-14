package client.operations

import com.google.inject.Inject
import protocol.client.ClientInterface
import server.control.{Disconnect, GetViews}

class SystemOperations @Inject() (
  registry: OperationRegistry,
  client: ClientInterface,
) {

  registry.registerOperation("help", () => {
    println("Available operations:")
    registry.getAllOperations.foreach { op =>
      println(op)
    }
    List()
  })

  registry.registerOperation("quit", () => {
    println("Shutdown server? (y/n)")
    scala.io.StdIn.readLine() match {
      case "y" => List(Disconnect(true))
      case _ => List(Disconnect(false))
    }
  })

  registry.registerOperation("views", () => {
    println("Active views:")
    client.sendQuery(GetViews).foreach(_.foreach(println))

    List()
  })

}
