package core.musicdata

import core.components.InputDevice
import core.idea.Idea
import javax.inject.Inject

class MusicDataStreamer @Inject() (stream: InputDevice, repository: MusicDataRepository) {

  def run(idea: Idea): Unit = stream.open.foreach(repository.put(idea, _))

}
