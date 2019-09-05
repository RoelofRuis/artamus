package client.operations

import com.google.inject.Inject
import server.control.Disconnect

class SystemOperations @Inject() (registry: OperationRegistry) {

  registry.registerOperation("quit", () => List(Disconnect(true)))

}
