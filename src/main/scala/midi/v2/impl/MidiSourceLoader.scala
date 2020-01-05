package midi.v2.impl

import javax.annotation.concurrent.NotThreadSafe
import javax.inject.{Inject, Singleton}
import midi.DeviceHash
import midi.v2.api.MidiIO

@NotThreadSafe
@Singleton
class MidiSourceLoader @Inject() (deviceLoader: MidiDeviceLoader) {

  private var loadedSources: Map[DeviceHash, MidiSource] = Map()

  def loadSource(hash: DeviceHash): MidiIO[MidiSource] = loadedSources.get(hash) match {
    case Some(source) => Right(source)
    case None => deviceLoader
      .loadDevice(hash)
      .flatMap { device =>
        MidiSourceReceiver.fromDevice(device)
          .map { source =>
            loadedSources += (hash -> source)
            source
          }
      }
  }

}
