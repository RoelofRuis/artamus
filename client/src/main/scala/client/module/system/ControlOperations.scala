package client.module.system

import api.Write.{Analyse, NewWorkspace, Render}
import client.module.Operations.{OperationRegistry, ServerOperation}
import javax.inject.Inject

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
