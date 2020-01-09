package client.io.midi

import client.operations.Operations.OperationRegistry
import com.google.inject.Inject
import javax.sound.midi.MidiDevice.Info
import midi.MidiResourceLoader
import patching.PatchPanel

class MidiOperations @Inject() (
  patchPanel: PatchPanel,
  loader: MidiResourceLoader,
  registry: OperationRegistry,
) {

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
    patchPanel.viewConnections.foreach(cable => println(cable))
  })

}
