package client.module.system

import domain.interact.Control.{Authenticate, Disconnect}
import client.module.Operations.{OperationRegistry, ServerOperation}
import client.util.StdIOTools
import javax.inject.Inject

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
