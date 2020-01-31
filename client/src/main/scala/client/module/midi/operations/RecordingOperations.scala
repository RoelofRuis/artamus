package client.module.midi.operations

import client.module.Operations.{OperationRegistry, ServerOperation}
import client.module.midi.MidiRecorder
import com.google.inject.Inject
import server.actions.recording.{ClearRecording, Quantize}
import server.actions.writing.Render

class RecordingOperations @Inject() (
  registry: OperationRegistry,
  recorder: MidiRecorder,
) {

  registry.server("rec", "midi", {
    recorder.activate()
    ServerOperation(ClearRecording())
  })

  registry.server("quantize", "midi", {
    recorder.deactivate()
    ServerOperation(Quantize(), Render)
  })

}
