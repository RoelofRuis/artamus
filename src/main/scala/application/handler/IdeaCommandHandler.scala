package application.handler

import application.api.Commands.{Command, CreateIdea, GetAllWithTracks}
import application.domain.Idea.Idea_ID
import application.domain.Track.Track_ID
import application.domain.repository.{IdeaRepository, TrackRepository}
import javax.inject.Inject

import scala.util.{Success, Try}

private[application] class IdeaCommandHandler @Inject() (
  ideaRepository: IdeaRepository,
  trackRepository: TrackRepository
) extends CommandHandler {

  def handle[Res]: PartialFunction[Command[Res], Try[Res]] = {
    case CreateIdea(title) => Success(ideaRepository.add(title).id)
    case GetAllWithTracks => Success(getAllWithTracks)
  }

  private def getAllWithTracks: Iterable[(Idea_ID, String, Iterable[Track_ID])] = {
    val tracks = trackRepository.getAll

    ideaRepository.getAll
      .map { idea =>
        (idea.id, idea.title, tracks.filter(_.ideaId == idea.id).map(_.id))
      }
  }

}
