package core.musicdata

import core.application.ServiceRegistry
import core.components.InputDevice
import core.idea.Idea
import javax.inject.Inject

class MusicDataStreamer @Inject() (stream: ServiceRegistry[InputDevice], repository: MusicDataRepository) {

  def run(idea: Idea): Unit = stream.getActive.open.foreach(repository.put(idea, _))

}
