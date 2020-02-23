package client.module.system

import client.module.Operations.{OperationRegistry, ServerOperation}
import domain.interact.Display.Render
import domain.interact.Write.{AnalyseChords, NewWorkspace}
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
