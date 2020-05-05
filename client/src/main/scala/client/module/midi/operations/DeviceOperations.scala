package client.module.midi.operations

import client.module.Operations.OperationRegistry
import com.google.inject.Inject
import javax.sound.midi.MidiDevice.Info
import client.midi.MidiResourceLoader
import patching.PatchPanel

class DeviceOperations @Inject() (
  patchPanel: PatchPanel,
  loader: MidiResourceLoader,
  registry: OperationRegistry,
) {

  registry.local("devices", "client/midi", {
    loader.viewAvailableDevices.foreach { case (hash, info: Info) =>
      println(s" > [$hash]")
      println(s"[${info.getName}]")
      println(s"[${info.getDescription}]")
      println(s"[${info.getVersion}]")
      println(s"[${info.getVendor}]")
      println()
    }
  })

  registry.local("patchboard", "client/midi", {
    patchPanel.viewConnections.foreach { case (description, id) =>
      println(s"Patch [${id.id}]\n[$description]")
      println()
    }
  })

}
