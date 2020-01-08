package client.io.midi

import client.io.IOLifetimeManager
import client.io.midi.nyt.MidiConnector
import javax.inject.{Inject, Named}
import midi.{DeviceHash, MidiResourceLoader}

class MidiIOLifetimeManager @Inject() (
  loader: MidiResourceLoader,
  connector: MidiConnector,
  @Named("midi-in") midiIn: DeviceHash,
  @Named("midi-out") midiOut: DeviceHash
) extends IOLifetimeManager {

  override def initializeAll(): Unit = {
    connector.connect(midiIn, midiOut)
  }

  override def closeAll(): Unit = {
    loader.closeAll()
  }

}
