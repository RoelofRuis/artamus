package client.module.midi.operations

import api.Record.{ClearRecording, Quantize}
import api.Write.Render
import client.module.Operations.{OperationRegistry, ServerOperation}
import client.module.midi.MidiRecorder
import client.util.StdIOTools
import com.google.inject.Inject
import domain.record.Quantizer

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

    ServerOperation(
      Quantize(None, rhythmOnly = false),
      Render
    )
  })

  registry.server("quantize+", "midi", {
    recorder.deactivate()

    val wholeNoteDuration: Int = StdIOTools.readInt("whole note duration")
    val rhythmOnly: Boolean = StdIOTools.readBool("rhythm only?")

    ServerOperation(
      Quantize(Some(Quantizer(wholeNoteDuration = wholeNoteDuration)), rhythmOnly),
      Render
    )
  })

}
