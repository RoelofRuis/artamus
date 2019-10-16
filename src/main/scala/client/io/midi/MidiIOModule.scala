package client.io.midi

import client.{MusicPlayer, MusicReader}
import midi.in.MidiMessageReader
import midi.out.SequenceWriter
import midi.{DeviceHash, loadReader, loadSequenceWriter}
import net.codingwell.scalaguice.ScalaPrivateModule

class MidiIOModule extends ScalaPrivateModule {

  private object MyDevices {
    val GervillSoftSynt: DeviceHash = "55c8a757"
    val FocusriteUSBMIDI_IN: DeviceHash = "658ef990"
    val FocusriteUSBMIDI_OUT: DeviceHash = "c7797746"
    val iRigUSBMIDI_IN: DeviceHash = "e98b95f2"
  }

  val ticksPerQuarter: Int = 4
  val midiOut: DeviceHash = MyDevices.FocusriteUSBMIDI_OUT
  val midiIn: DeviceHash = MyDevices.iRigUSBMIDI_IN

  override def configure(): Unit = {
    // READING
    // TODO: Remove '.get', wrap with resource management!
    bind[MidiMessageReader].toInstance(loadReader(midiIn).get)
    bind[MusicReader].to[MidiMusicReader].asEagerSingleton()

    // WRITING (playback)
    // TODO: Remove '.get', wrap with resource management!
    bind[SequenceWriter].toInstance(loadSequenceWriter(midiOut, ticksPerQuarter).get)
    bind[MusicPlayer].to[MidiMusicPlayer].asEagerSingleton()

    expose[MusicPlayer]
    expose[MusicReader]
  }

}
