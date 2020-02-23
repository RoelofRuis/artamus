package client.module.system

import domain.interact.Write.{AnalyseChords, NewWorkspace, Render}
import client.module.Operations.{OperationRegistry, ServerOperation}
import javax.inject.Inject

class ControlOperations @Inject() (
  registry: OperationRegistry,
) {

  registry.server("analyse", "track", {
    ServerOperation(AnalyseChords, Render)
  })

  registry.server("new", "workspace", {
    ServerOperation(NewWorkspace, Render)
  })

}
