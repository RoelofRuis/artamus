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

    val `type`: String = StdIOTools.readString("quantize type:\n+     = advanced\ng     = grid\nother = quick")
    val quantizer = `type` match {
      case "g" =>
        val gridSpacing = StdIOTools.readInt("Grid spacing of 1/_?")
        val gridSize = Rational.reciprocal(gridSpacing)
        Quantize(
          Some(Quantizer(consideredLengths=Set(gridSize), interClusterDistance=2000)),
          rhythmOnly = false,
          Duration(gridSize)
        )

      case "+" =>
        val wholeNoteDuration: Int = StdIOTools.readInt("whole note duration")
        val rhythmOnly: Boolean = StdIOTools.readBool("rhythm only?")
        Quantize(Some(Quantizer(wholeNoteDuration = wholeNoteDuration)), rhythmOnly)

      case _ =>
        Quantize(None, rhythmOnly = false)
    }

    ServerOperation(quantizer, Render)
  })

}
