package application.handler

import application.api.Commands.{CreateIdea, GetAllWithTracks}
import application.domain.Idea.Idea_ID
import application.domain.Track.Track_ID
import application.domain.repository.{IdeaRepository, TrackRepository}
import application.interact.SynchronousCommandBus
import javax.inject.Inject

import scala.util.Success

private[application] class IdeaCommandHandler @Inject() (
  bus: SynchronousCommandBus,
  ideaRepository: IdeaRepository,
  trackRepository: TrackRepository
) {

  bus.subscribeHandler(Handler[CreateIdea](c => Success(ideaRepository.add(c.title).id)))
  bus.subscribeHandler(Handler[GetAllWithTracks.type](_ => Success(getAllWithTracks)))

  private def getAllWithTracks: Iterable[(Idea_ID, String, Iterable[Track_ID])] = {
    val tracks = trackRepository.getAll

    ideaRepository.getAll
      .map { idea =>
        (idea.id, idea.title, tracks.filter(_.ideaId == idea.id).map(_.id))
      }
  }

}
