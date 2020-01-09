package client.io.midi

import client.io.IOLifetimeManager
import client.{MusicPlayer, MusicReader}
import midi.read.MidiInput
import midi.write.MidiSequenceWriter
import midi.{DeviceHash, MidiResourceLoader}
import net.codingwell.scalaguice.ScalaPrivateModule
import patching.PatchPanel

class MidiIOModule extends ScalaPrivateModule {

  private object MyDevices {
    val GervillSoftSynt: DeviceHash = "55c8a757"
    val FocusriteUSBMIDI_IN: DeviceHash = "658ef990"
    val FocusriteUSBMIDI_OUT: DeviceHash = "c7797746"
    val iRigUSBMIDI_IN: DeviceHash = "e98b95f2"
  }

  override def configure(): Unit = {
    bind[PatchPanel].asEagerSingleton()
    bind[MidiResourceLoader].asEagerSingleton()
    bind[MidiPatchPanel]

    bind[DeviceHash].annotatedWithName("midi-in").toInstance(MyDevices.iRigUSBMIDI_IN)
    bind[MidiInput].to[ReadableMidiInput]
    bind[MusicReader].to[MidiMusicReader]

    bind[DeviceHash].annotatedWithName("midi-out").toInstance(MyDevices.FocusriteUSBMIDI_OUT)
    bind[MidiSequenceWriter].to[SequencePlayer]
    bind[MusicPlayer].to[MidiMusicPlayer]

    bind[IOLifetimeManager].to[MidiIOLifetimeManager]

    expose[MusicPlayer]
    expose[MusicReader]
    expose[IOLifetimeManager]
  }

}
