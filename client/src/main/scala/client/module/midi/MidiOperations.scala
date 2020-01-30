package client.module.midi

import client.module.Operations.{OperationRegistry, ServerOperation}
import com.google.inject.Inject
import javax.sound.midi.MidiDevice.Info
import midi.MidiResourceLoader
import patching.PatchPanel
import server.actions.recording.{ClearRecording, Quantize}
import server.actions.writing.Render

class MidiOperations @Inject() (
  patchPanel: PatchPanel,
  loader: MidiResourceLoader,
  registry: OperationRegistry,
) {

  registry.server("rec", "midi", {
    ServerOperation(ClearRecording())
  })

  registry.server("quantize", "midi", {
    ServerOperation(Quantize(), Render)
  })

  registry.local("devices", "midi", {
    loader.viewAvailableDevices.foreach { case (hash, info: Info) =>
      println(s" > [$hash]")
      println(s"[${info.getName}]")
      println(s"[${info.getDescription}]")
      println(s"[${info.getVersion}]")
      println(s"[${info.getVendor}]")
      println()
    }
  })

  registry.local("patchboard", "midi", {
    patchPanel.viewConnections.foreach { case (description, id) =>
      println(s"Patch [${id.id}]\n[$description]")
      println()
    }
  })

}
