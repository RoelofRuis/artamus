package client.module.midi

import client.ModuleLifetimeHooks
import client.module.midi.operations.{DebugOperations, DeviceOperations, EditOperations, PlaybackOperations}
import midi.read.MidiInput
import midi.write.MidiSequenceWriter
import midi.{DeviceHash, MidiResourceLoader}
import net.codingwell.scalaguice.ScalaPrivateModule
import patching.PatchPanel

class MidiModule extends ScalaPrivateModule {

  private object MyDevices {
    val GervillSoftSynt: DeviceHash = "55c8a757"
    val FocusriteUSBMIDI_IN: DeviceHash = "658ef990"
    val FocusriteUSBMIDI_OUT: DeviceHash = "c7797746"
    val iRigUSBMIDI_IN: DeviceHash = "e98b95f2"
  }

  override def configure(): Unit = {
    bind[ModuleLifetimeHooks].to[MidiLifetimeHooks]

    bind[DeviceOperations].asEagerSingleton()
    bind[DebugOperations].asEagerSingleton()
    bind[EditOperations].asEagerSingleton()
    bind[PlaybackOperations].asEagerSingleton()

    bind[PatchPanel].asEagerSingleton()
    bind[MidiResourceLoader].asEagerSingleton()
    bind[MidiRecorder].asEagerSingleton()

    bind[DeviceHash].annotatedWithName("midi-in").toInstance(MyDevices.iRigUSBMIDI_IN)
    bind[MidiInput].to[ReadableMidiInput]

    bind[DeviceHash].annotatedWithName("midi-out").toInstance(MyDevices.FocusriteUSBMIDI_OUT)
    bind[MidiSequenceWriter].to[SequencePlayer]

    expose[ModuleLifetimeHooks]
  }

}
