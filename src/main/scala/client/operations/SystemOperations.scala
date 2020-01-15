package client.operations

import client.io.StdIOTools
import client.operations.Operations.{OperationRegistry, ServerOperation}
import javax.inject.Inject
import server.actions.control.{Authenticate, Disconnect}

class SystemOperations @Inject() (
  registry: OperationRegistry,
) {

  registry.server("auth", "system", {
    val userName = StdIOTools.readString("User Name:")
    ServerOperation(Authenticate(userName))
  })

  registry.local("help", "system", {
    println("Available operations:")
    val tokens = registry.viewRegisteredTokens

    tokens.toList.sortBy(t => t.registrar + t.command).foreach{ token =>
      println(s"[${token.registrar}] ${token.command}")
    }
  })

  registry.server("quit", "system", {
    ServerOperation(Disconnect())
  })

}
