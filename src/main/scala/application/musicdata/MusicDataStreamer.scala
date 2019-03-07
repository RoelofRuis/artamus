package application.musicdata

import application.core.ServiceRegistry
import application.components.InputDevice
import application.idea.Idea
import javax.inject.Inject

class MusicDataStreamer @Inject() (input: ServiceRegistry[InputDevice], repository: GridRepository) {

  def run(idea: Idea): Unit = input.map { device =>
    device.readData.foreach(data => repository.store(idea.id, data))
  }
}
