package client.operations

import com.google.inject.Inject
import protocol.ClientInterface
import server.control.Disconnect

class SystemOperations @Inject() (
  registry: OperationRegistry,
  client: ClientInterface,
) {

  registry.registerOperation(OperationToken("help", "system"), () => {
    println("Available operations:")
    val tokens = registry.getRegisteredTokens

    tokens.toList.sortBy(t => t.registrar + t.command).foreach{ token =>
      println(s"[${token.registrar}] ${token.command}")
    }

    List()
  })

  registry.registerOperation(OperationToken("stop-server", "system"), () => {
    List(Disconnect(true))
  })

}
