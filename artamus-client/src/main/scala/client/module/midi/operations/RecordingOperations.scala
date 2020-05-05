package client.module.midi.operations

import client.module.Operations.{OperationRegistry, ServerOperation}
import client.module.StdIOTools
import client.module.midi.MidiRecorder
import com.google.inject.Inject
import artamus.core.api.Display.Render
import artamus.core.api.Record.{ClearRecording, Quantize, SetFormalisationProfile}
import artamus.core.math.Rational
import artamus.core.math.temporal.Duration
import artamus.core.ops.formalise.{Quantizer, FormalisationProfile}

class RecordingOperations @Inject() (
  registry: OperationRegistry,
  recorder: MidiRecorder,
) {

  registry.server("rec", "midi", {
    recorder.activate()
    ServerOperation(ClearRecording())
  })

  registry.server("quantize", "midi", {
    ServerOperation(Quantize(), Render)
  })

  registry.server("set-formalisation", "midi", {
    recorder.deactivate()

    val `type`: String = StdIOTools.readString("quantize type:\n+     = advanced\ng     = grid\nother = quick")
    val recordTransfer = `type` match {
      case "g" =>
        val gridSpacing = StdIOTools.readInt("Grid spacing of 1/_?")
        val gridSize = Rational.reciprocal(gridSpacing)
        FormalisationProfile(
          Quantizer(consideredLengths=Set(gridSize), interClusterDistance=2000),
          rhythmOnly = false,
          Duration(gridSize)
        )

      case "+" =>
        val wholeNoteDuration: Int = StdIOTools.readInt("whole note duration")
        val rhythmOnly: Boolean = StdIOTools.readBool("rhythm only?")
        FormalisationProfile(Quantizer(wholeNoteDuration = wholeNoteDuration), rhythmOnly, Duration(Rational(1, 4)))


      case _ =>
        FormalisationProfile(Quantizer(), rhythmOnly = false, Duration(Rational(1, 4)))
    }

    ServerOperation(SetFormalisationProfile(recordTransfer), Render)
  })

}
