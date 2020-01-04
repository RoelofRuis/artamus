package client.io.midi

import client.{MusicPlayer, MusicReader}
import com.google.inject.Provides
import javax.inject.{Named, Singleton}
import midi.in.MidiMessageReader
import midi.out.SequenceWriter
import midi.v2.api.MidiInput
import midi.v2.impl.{MidiSourceLoader, SingleDeviceMidiInput}
import midi.{DeviceHash, loadReader, loadSequenceWriter}
import net.codingwell.scalaguice.ScalaPrivateModule

class MidiIOModule2 extends ScalaPrivateModule {

  private object MyDevices {
    val GervillSoftSynt: DeviceHash = "55c8a757"
    val FocusriteUSBMIDI_IN: DeviceHash = "658ef990"
    val FocusriteUSBMIDI_OUT: DeviceHash = "c7797746"
    val iRigUSBMIDI_IN: DeviceHash = "e98b95f2"
  }

  val midiOut: DeviceHash = MyDevices.FocusriteUSBMIDI_OUT
  val midiIn: DeviceHash = MyDevices.iRigUSBMIDI_IN

  override def configure(): Unit = {
    bind[DeviceHash].annotatedWithName("midi-in").toInstance(MyDevices.iRigUSBMIDI_IN)
    bind[MidiSourceLoader].toInstance(new MidiSourceLoader)

    // READING
    // TODO: Remove '.get', wrap with resource management!
    bind[MidiMessageReader].toInstance(loadReader(midiIn).get)
    bind[MusicReader].to[MidiMusicReader].asEagerSingleton()

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
