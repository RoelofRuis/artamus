package midi

import javax.inject.{Inject, Singleton}

@Singleton
class MidiConnector @Inject() (loader: MidiResourceLoader) {

  def connect(midiIn: DeviceHash, midiOut: DeviceHash): MidiIO[Unit] = {
    for {
      inDevice <- loader.loadDevice(midiIn)
      outDevice <- loader.loadDevice(midiOut)
      _ <- MidiIO(inDevice.getTransmitter.setReceiver(outDevice.getReceiver))
    } yield ()
  }

}
