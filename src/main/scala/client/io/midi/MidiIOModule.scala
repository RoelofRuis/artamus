package client.io.midi

import client.io.IOLifetimeManager
import client.io.midi.nyt.{MidiConnector, ReadableMidiInput}
import client.{MusicPlayer, MusicReader}
import com.google.inject.Provides
import javax.inject.{Named, Singleton}
import midi.DeviceHash
import midi.out.api.MidiOutput
import midi.out.impl.{MidiSinkLoader, SingleDeviceMidiOutput}
import midi.receiver.MidiInput
import net.codingwell.scalaguice.ScalaPrivateModule
import patchpanel.PatchPanel

class MidiIOModule extends ScalaPrivateModule {

  private object MyDevices {
    val GervillSoftSynt: DeviceHash = "55c8a757"
    val FocusriteUSBMIDI_IN: DeviceHash = "658ef990"
    val FocusriteUSBMIDI_OUT: DeviceHash = "c7797746"
    val iRigUSBMIDI_IN: DeviceHash = "e98b95f2"
  }

  val midiOut: DeviceHash = MyDevices.FocusriteUSBMIDI_OUT

  override def configure(): Unit = {
    bind[PatchPanel].asEagerSingleton()

    bind[DeviceHash].annotatedWithName("midi-in").toInstance(MyDevices.iRigUSBMIDI_IN)
    bind[MidiInput].to[ReadableMidiInput]

    bind[MusicReader].to[MidiMusicReader]


    bind[DeviceHash].annotatedWithName("midi-out").toInstance(MyDevices.FocusriteUSBMIDI_OUT)
    bind[MidiSinkLoader]
    bind[MusicPlayer].to[MidiMusicPlayer]

    bind[MidiConnector]
    bind[IOLifetimeManager].to[MidiIOLifetimeManager]

    expose[MusicPlayer]
    expose[MusicReader]
    expose[IOLifetimeManager]
  }

  @Provides
  @Named("default-midi-out")
  @Singleton
  def provideMidiOut(@Named("midi-out") midiOut: DeviceHash, loader: MidiSinkLoader): MidiOutput = {
    new SingleDeviceMidiOutput(midiOut, loader)
  }

}
