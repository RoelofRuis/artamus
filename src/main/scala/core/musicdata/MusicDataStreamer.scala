package core.musicdata

import core.application.ServiceRegistry
import core.components.InputDevice
import core.idea.Idea
import javax.inject.Inject

class MusicDataStreamer @Inject() (input: ServiceRegistry[InputDevice], repository: MusicDataRepository) {

  def run(idea: Idea): Unit = input.map(_.open.foreach(repository.put(idea, _)))

}
