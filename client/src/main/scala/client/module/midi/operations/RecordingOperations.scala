package client.module.midi.operations

import client.module.Operations.{OperationRegistry, ServerOperation}
import com.google.inject.Inject
import server.actions.recording.{ClearRecording, Quantize}
import server.actions.writing.Render

class RecordingOperations @Inject() (
  registry: OperationRegistry,
) {

  registry.server("rec", "midi", {
    ServerOperation(ClearRecording())
  })

  registry.server("quantize", "midi", {
    ServerOperation(Quantize(), Render)
  })

}
