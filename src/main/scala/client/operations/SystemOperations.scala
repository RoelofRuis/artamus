package client.operations

import client.io.StdIOTools
import com.google.inject.Inject
import server.actions.control.{Authenticate, Disconnect}

class SystemOperations @Inject() (
  registry: OperationRegistry,
) {

  registry.registerOperation(OperationToken("auth", "system"), () => {
    val userName = StdIOTools.readString("User Name:")
    Operation.list(Authenticate(userName))
  })

  registry.registerOperation(OperationToken("help", "system"), () => {
    println("Available operations:")
    val tokens = registry.getRegisteredTokens

    tokens.toList.sortBy(t => t.registrar + t.command).foreach{ token =>
      println(s"[${token.registrar}] ${token.command}")
    }

    Operation.none
  })

  registry.registerOperation(OperationToken("quit", "system"), () => {
    Operation.list(Disconnect())
  })

}
