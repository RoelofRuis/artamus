package application.controller

import application.component.ServiceRegistry
import application.model.Idea
import application.model.Idea.ID
import application.model.repository.{IdeaRepository, TrackRepository}
import application.ports.{InputDevice, Logger, PlaybackDevice}
import javax.inject.Inject

trait IdeaController {

  def getAll: Vector[Idea]

  def create(title: String): Idea

  def play(id: ID): Boolean

}

private[application] class IdeaControllerImpl @Inject() (
  ideaRepository: IdeaRepository,
  trackRepository: TrackRepository,
  input: ServiceRegistry[InputDevice],
  playback: ServiceRegistry[PlaybackDevice],
  logger: ServiceRegistry[Logger],
) extends IdeaController {

  def getAll: Vector[Idea] = ideaRepository.getAll

  // TODO: Try[Idea] as return value
  def create(title: String): Idea = {
    val idea = ideaRepository.add(title)

    input.use { device =>
        device.readUnquantized.foreach(trackRepository.storeUnquantized(idea.id, _))
    }

    idea
  }

  def play(id: ID): Boolean = {
    trackRepository.retrieve(id) match {
      case None => false
      case Some(data) =>
        playback.use(_.playbackUnquantized(data))
        true
    }
  }

}
