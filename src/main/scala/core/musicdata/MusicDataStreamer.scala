package core.musicdata

import core.components.MusicDataStream
import core.idea.Idea
import javax.inject.Inject

class MusicDataStreamer @Inject() (stream: MusicDataStream, repository: MusicDataRepository) {

  def run(idea: Idea): Unit = stream.open.foreach(repository.put(idea, _))

}
