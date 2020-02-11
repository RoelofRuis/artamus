package client.module.midi.operations

import domain.interact.Record.{ClearRecording, Quantize}
import domain.interact.Write.Render
import client.module.Operations.{OperationRegistry, ServerOperation}
import client.module.midi.MidiRecorder
import client.util.StdIOTools
import com.google.inject.Inject
import domain.math.Rational
import domain.math.temporal.Duration
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

  registry.server("grid-quantize", "midi", {
    recorder.deactivate()

    val gridSpacing = StdIOTools.readInt("Grid spacing of 1/_?")
    val gridSize = Rational.reciprocal(gridSpacing)

    ServerOperation(
      Quantize(
        Some(Quantizer(consideredLengths=Set(gridSize))),
        rhythmOnly = false,
        Duration(gridSize)
      ),
      Render
    )
  })

}
