package client.operations

import com.google.inject.Inject
import server.control.Disconnect

class SystemOperations @Inject() (registry: OperationRegistry) {

  registry.registerOperation("quit", () => {
    println("Shutdown server? (y/n)")
    scala.io.StdIn.readLine() match {
      case "y" => List(Disconnect(true))
      case _ => List(Disconnect(false))
    }
  })

}
