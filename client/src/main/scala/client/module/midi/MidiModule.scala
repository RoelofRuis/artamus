package client.module.midi

import client.ModuleLifetimeHooks
import client.module.midi.operations.{DebugOperations, DeviceOperations, EditOperations, PlaybackOperations, RecordingOperations}
import client.midi.read.MidiInput
import client.midi.write.MidiSequenceWriter
import client.midi.MidiResourceLoader
import midi.DeviceHash
import net.codingwell.scalaguice.ScalaPrivateModule
import client.patching.PatchPanel

class MidiModule extends ScalaPrivateModule {

  private object MyDevices {
    val GervillSoftSynt: DeviceHash = "55c8a757"
    val FocusriteUSBMIDI_IN: DeviceHash = "658ef990"
    val FocusriteUSBMIDI_OUT: DeviceHash = "c7797746"
    val iRigUSBMIDI_IN: DeviceHash = "e98b95f2"
    val SamsonConspiracyMIDI_IN: DeviceHash = "b2a17aab"
    val SamsonConspiracyMIDI_OUT: DeviceHash = "a4424e74"
  }

  override def configure(): Unit = {
    bind[ModuleLifetimeHooks].to[MidiLifetimeHooks]

    bind[DeviceOperations].asEagerSingleton()
    bind[DebugOperations].asEagerSingleton()
    bind[EditOperations].asEagerSingleton()
    bind[PlaybackOperations].asEagerSingleton()
    bind[RecordingOperations].asEagerSingleton()

    bind[PatchPanel].asEagerSingleton()
    bind[MidiResourceLoader].asEagerSingleton()
    bind[MidiRecorder].asEagerSingleton()

    bind[DeviceHash].annotatedWithName("client.midi-in").toInstance(MyDevices.iRigUSBMIDI_IN)
    bind[MidiInput].to[ReadableMidiInput]

    bind[DeviceHash].annotatedWithName("client.midi-out").toInstance(MyDevices.FocusriteUSBMIDI_OUT)
    bind[MidiSequenceWriter].to[SequencePlayer]

    bind[DeviceHash].annotatedWithName("client.midi-control-in").toInstance(MyDevices.SamsonConspiracyMIDI_IN)
    bind[MidiControlSignals].asEagerSingleton()

    expose[ModuleLifetimeHooks]
  }

}
