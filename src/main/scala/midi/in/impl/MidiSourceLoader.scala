package midi.in.impl

import javax.annotation.concurrent.NotThreadSafe
import javax.inject.{Inject, Singleton}
import midi.{DeviceHash, MidiIO, MidiResourceLoader}

@NotThreadSafe
@Singleton
class MidiSourceLoader @Inject() (resourceLoader: MidiResourceLoader) {

  private var loadedSources: Map[DeviceHash, MidiSource] = Map()

  def loadSource(hash: DeviceHash): MidiIO[MidiSource] = loadedSources.get(hash) match {
    case Some(source) => MidiIO(source)
    case None => resourceLoader
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
