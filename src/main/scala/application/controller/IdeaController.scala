package application.controller

import application.component.ServiceRegistry
import application.model.Idea
import application.model.Idea.ID
import application.model.repository.{GridRepository, IdeaRepository}
import application.ports.{InputDevice, PlaybackDevice}
import javax.inject.Inject

trait IdeaController {

  def getAll: Vector[Idea]

  def create(title: String): Idea

  def play(id: ID): Boolean

}

private[application] class IdeaControllerImpl @Inject() (
  ideaRepository: IdeaRepository,
  gridRepository: GridRepository,
  input: ServiceRegistry[InputDevice],
  playback: ServiceRegistry[PlaybackDevice]
) extends IdeaController {

  def getAll: Vector[Idea] = ideaRepository.getAll

  // TODO: Try[Idea] as return value
  def create(title: String): Idea = {
    val idea = ideaRepository.add(title)

    input.use { device =>
      device.readData.foreach(data => gridRepository.store(idea.id, data))
    }

    idea
  }

  def play(id: ID): Boolean = {
    gridRepository.retrieve(id) match {
      case None => false
      case Some(data) =>
        playback.use(_.play(data))
        true
    }
  }

}
