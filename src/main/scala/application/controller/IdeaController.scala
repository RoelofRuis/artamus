package application.controller

import application.component.ServiceRegistry
import application.model.Idea
import application.model.repository.{GridRepository, IdeaRepository}
import application.ports.InputDevice
import com.google.inject.Inject

class IdeaController @Inject() (
  ideaRepository: IdeaRepository,
  repository: GridRepository,
  input: ServiceRegistry[InputDevice],
) {

  // TODO: Try[Idea] as return value
  def create(title: String): Idea = {
    val idea = ideaRepository.add(title)

    input.map { device =>
      device.readData.foreach(data => repository.store(idea.id, data))
    }

    idea
  }

}
