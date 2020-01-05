package client.io.midi

import client.{MusicPlayer, MusicReader}
import com.google.inject.Provides
import javax.inject.{Named, Singleton}
import midi.v2.DeviceHash
import midi.v2.in.api.MidiInput
import midi.v2.in.impl.{MidiSourceLoader, SingleDeviceMidiInput}
import midi.v2.out.api.MidiOutput
import midi.v2.out.impl.{MidiSinkLoader, SingleDeviceMidiOutput}
import net.codingwell.scalaguice.ScalaPrivateModule

class MidiIOModule extends ScalaPrivateModule {

  private object MyDevices {
    val GervillSoftSynt: DeviceHash = "55c8a757"
    val FocusriteUSBMIDI_IN: DeviceHash = "658ef990"
    val FocusriteUSBMIDI_OUT: DeviceHash = "c7797746"
    val iRigUSBMIDI_IN: DeviceHash = "e98b95f2"
  }

  val midiOut: DeviceHash = MyDevices.FocusriteUSBMIDI_OUT

  override def configure(): Unit = {
    bind[DeviceHash].annotatedWithName("midi-in").toInstance(MyDevices.iRigUSBMIDI_IN)
    bind[MidiSourceLoader]
    bind[MusicReader].to[MidiMusicReader]

    bind[DeviceHash].annotatedWithName("midi-out").toInstance(MyDevices.FocusriteUSBMIDI_OUT)
    bind[MidiSinkLoader]
    bind[MusicPlayer].to[MidiMusicPlayer].asEagerSingleton()

    expose[MusicPlayer]
    expose[MusicReader]
  }

  @Provides
  @Named("default-midi-out")
  @Singleton
  def provideMidiOut(@Named("midi-out") midiOut: DeviceHash, loader: MidiSinkLoader): MidiOutput = {
    new SingleDeviceMidiOutput(midiOut, loader)
  }

  @Provides
  @Named("default-midi-in")
  @Singleton
  def provideMidiIn(@Named("midi-in") midiIn: DeviceHash, loader: MidiSourceLoader): MidiInput = {
    new SingleDeviceMidiInput(midiIn, loader)
  }

}
