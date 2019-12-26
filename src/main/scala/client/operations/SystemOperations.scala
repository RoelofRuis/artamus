package client.operations

import client.io.StdIOTools
import com.google.inject.Inject
import server.actions.control.{Authenticate, Disconnect}

class SystemOperations @Inject() (
  registry: OperationRegistry,
) {

  registry.registerOperation(OperationToken("auth", "system"), () => {
    val userName = StdIOTools.readString("User Name:")
    List(Authenticate(userName))
  })

  registry.registerOperation(OperationToken("help", "system"), () => {
    println("Available operations:")
    val tokens = registry.getRegisteredTokens

    tokens.toList.sortBy(t => t.registrar + t.command).foreach{ token =>
      println(s"[${token.registrar}] ${token.command}")
    }

    List()
  })

  registry.registerOperation(OperationToken("quit", "system"), () => {
    println("Shutdown server? (y/n)")
    scala.io.StdIn.readLine() match {
      case "y" => List(Disconnect(true))
      case _ => List(Disconnect(false))
    }
  })

}
