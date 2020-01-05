package client.io.midi

import client.io.IOLifetimeManager
import javax.inject.{Inject, Named}
import midi.{DeviceHash, MidiConnector, MidiResourceLoader}

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
