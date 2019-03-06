package core.musicdata

import core.application.ServiceRegistry
import core.components.InputDevice
import core.idea.Idea
import javax.inject.Inject

class MusicDataStreamer @Inject() (input: ServiceRegistry[InputDevice], repository: PartRepository) {

  def run(idea: Idea): Unit = input.map(device => repository.store(idea.id, device.open))

}
