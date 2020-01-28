package client.module.system

import client.module.Operations.{OperationRegistry, ServerOperation}
import javax.inject.Inject
import server.actions.writing.{Analyse, NewWorkspace, Render}

class ControlOperations @Inject() (
  registry: OperationRegistry,
) {

  registry.server("analyse", "track", {
    ServerOperation(Analyse, Render)
  })

  registry.server("new", "workspace", {
    ServerOperation(NewWorkspace, Render)
  })

}
