package client.module.midi.operations

import client.module.Operations.{OperationRegistry, ServerOperation}
import client.module.StdIOTools
import client.module.midi.MidiRecorder
import com.google.inject.Inject
import artamus.core.api.Display.Render
import artamus.core.api.Record.{ClearRecording, Quantize, SetRecordTransfer}
import nl.roelofruis.math.Rational
import nl.roelofruis.math.temporal.Duration
import artamus.core.ops.formalise.{Quantizer, RecordTransfer}

class RecordingOperations @Inject() (
  registry: OperationRegistry,
  recorder: MidiRecorder,
) {

  registry.server("rec", "client/midi", {
    recorder.activate()
    ServerOperation(ClearRecording())
  })

  registry.server("quantize", "client/midi", {
    ServerOperation(Quantize(), Render)
  })

  registry.server("set-quantization", "client/midi", {
    recorder.deactivate()

    val `type`: String = StdIOTools.readString("quantize type:\n+     = advanced\ng     = grid\nother = quick")
    val recordTransfer = `type` match {
      case "g" =>
        val gridSpacing = StdIOTools.readInt("Grid spacing of 1/_?")
        val gridSize = Rational.reciprocal(gridSpacing)
        RecordTransfer(
          Quantizer(consideredLengths=Set(gridSize), interClusterDistance=2000),
          rhythmOnly = false,
          Duration(gridSize)
        )

      case "+" =>
        val wholeNoteDuration: Int = StdIOTools.readInt("whole note duration")
        val rhythmOnly: Boolean = StdIOTools.readBool("rhythm only?")
        RecordTransfer(Quantizer(wholeNoteDuration = wholeNoteDuration), rhythmOnly, Duration(Rational(1, 4)))


      case _ =>
        RecordTransfer(Quantizer(), rhythmOnly = false, Duration(Rational(1, 4)))
    }

    ServerOperation(SetRecordTransfer(recordTransfer), Render)
  })

}
