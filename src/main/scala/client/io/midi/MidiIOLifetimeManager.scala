package client.io.midi

import client.io.IOLifetimeManager
import javax.inject.{Inject, Named}
import midi.{DeviceHash, MidiResourceLoader}

class MidiIOLifetimeManager @Inject() (
  loader: MidiResourceLoader,
  patchPanel: MidiPatchPanel,
  @Named("midi-in") midiIn: DeviceHash,
  @Named("midi-out") midiOut: DeviceHash
) extends IOLifetimeManager {

  override def initializeAll(): Unit = {
    patchPanel.connect(midiIn, midiOut) match {
      case Left(ex) => ex.cause.printStackTrace()
      case Right(_) =>
    }
  }

  override def closeAll(): Unit = {
    loader.closeAll()
  }

}
