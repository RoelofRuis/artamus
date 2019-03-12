package application.controller

import application.component.ServiceRegistry
import application.model.{Idea, TrackType, Unquantized}
import application.model.Idea.ID
import application.model.repository.{IdeaRepository, TrackRepository}
import application.ports.{InputDevice, PlaybackDevice}
import javax.inject.Inject

trait IdeaController {

  def getAll: Vector[Idea]

  def create(title: String): Idea

  def play(id: ID, trackType: TrackType): Boolean

}

private[application] class IdeaControllerImpl @Inject() (
  ideaRepository: IdeaRepository,
  trackRepository: TrackRepository,
  input: ServiceRegistry[InputDevice],
  playback: ServiceRegistry[PlaybackDevice],
) extends IdeaController {

  def getAll: Vector[Idea] = ideaRepository.getAll

  private final val TICKS_PER_QUARTER = 100

  // TODO: Try[Idea] as return value
  def create(title: String): Idea = {
    val idea = ideaRepository.add(title)

    input.use { device =>
        device.read(TICKS_PER_QUARTER)
          .foreach(trackRepository.store(idea.id, Unquantized, _))
    }

    idea
  }

  def play(id: ID, trackType: TrackType): Boolean = {
    trackRepository.retrieve(id, trackType) match {
      case None => false
      case Some(data) =>
        playback.use(_.playback(data))
        true
    }
  }

}
