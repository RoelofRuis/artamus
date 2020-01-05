package client.io.midi

import client.{MusicPlayer, MusicReader}
import com.google.inject.Provides
import javax.inject.{Named, Singleton}
import midi.out.SequenceWriter
import midi.v2.in.api.MidiInput
import midi.v2.in.impl.{MidiSourceLoader, SingleDeviceMidiInput}
import midi.{DeviceHash, loadSequenceWriter}
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

    // WRITING (playback)
    // TODO: Remove '.get', wrap with resource management!
    bind[SequenceWriter].toInstance(loadSequenceWriter(midiOut).get)
    bind[MusicPlayer].to[MidiMusicPlayer].asEagerSingleton()

    expose[MusicPlayer]
    expose[MusicReader]
  }

  @Provides
  @Named("default-midi-in")
  @Singleton
  def provideMidiIn(@Named("midi-in") midiIn: DeviceHash, loader: MidiSourceLoader): MidiInput = {
    new SingleDeviceMidiInput(midiIn, loader)
  }

}
