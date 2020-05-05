package client.module.system

import client.module.Operations.{OperationRegistry, ServerOperation}
import nl.roelofruis.artamus.core.api.Display.Render
import nl.roelofruis.artamus.core.api.Write.{AnalyseChords, NewWorkspace}
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
