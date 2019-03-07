package application.musicdata

import application.components.ServiceRegistry
import application.idea.Idea
import application.ports.InputDevice
import javax.inject.Inject

class MusicDataStreamer @Inject() (input: ServiceRegistry[InputDevice], repository: GridRepository) {

  def run(idea: Idea): Unit = input.map { device =>
    device.readData.foreach(data => repository.store(idea.id, data))
  }
}
