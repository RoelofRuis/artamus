package core.musicdata

import core.components.MusicDataStream
import javax.inject.Inject

class MusicDataStreamer @Inject() (stream: MusicDataStream, repository: MusicDataRepository) {

  def run(): Unit = stream.open.foreach(repository.put)

}
