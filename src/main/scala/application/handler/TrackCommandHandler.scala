package application.handler

import application.api.Commands._
import application.api.Events.PlaybackRequest
import application.interact.DomainEventBus
import application.domain.Idea.Idea_ID
import application.domain.Track.{Track_ID, Unquantized}
import application.domain.repository.TrackRepository
import application.service.quantization.TrackQuantizer
import application.service.quantization.TrackQuantizer.Params
import application.service.recording.RecordingManager
import javax.inject.{Inject, Named}

import scala.util.{Failure, Success, Try}

// TODO: see if TrackQuantizer and TicksPerQuarter can be moved to a separate service/controller
class TrackCommandHandler @Inject() (
  trackRepository: TrackRepository,
  quantizer: TrackQuantizer,
  @Named("TicksPerQuarter") recordingResolution: Int,
  recordingManager: RecordingManager,
  eventBus: DomainEventBus
) extends CommandHandler {

  override def handle[Res]: PartialFunction[Command[Res], Try[Res]] = {
    case StartRecording => recordingManager.startRecording
    case StoreRecorded(ideaId) => storeRecorded(ideaId)
    case Quantize(trackId, subdivision, gridErrorMultiplier) => quantize(trackId, subdivision, gridErrorMultiplier)
    case Play(trackId) => play(trackId)
  }

  private def storeRecorded(ideaId: Idea_ID): Try[(Track_ID, Int)] = {
    recordingManager.stopRecording.map { case (ticks, elements) =>
      trackRepository.add(
        ideaId,
        Unquantized,
        ticks,
        elements
      )
    }.map(t => (t.id, t.elements.size))
  }

  private def play(id: Track_ID): Try[Unit] = {
    trackRepository.get(id).map { track =>
      eventBus.publish(PlaybackRequest(track))
    }
  }

  def quantize(id: Track_ID, subdivision: Int, gridErrorMultiplier: Int): Try[Track_ID] = {
    val quantizationParams = Params(
      recordingResolution / 16,
      recordingResolution * 2,
      gridErrorMultiplier,
      subdivision
    )

    trackRepository.get(id)
      .map { track =>
        val (newTicksPerQuarter, newElements) = quantizer.quantize(track, quantizationParams)

        trackRepository.add(
          track.ideaId,
          track.trackType,
          newTicksPerQuarter,
          newElements
        )
      }.map(_.id)
  }
}
