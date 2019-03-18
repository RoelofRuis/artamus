package application.controller

import application.command.Command
import application.command.TrackCommand._
import application.component.ServiceRegistry
import application.model.Idea.Idea_ID
import application.model.Track
import application.model.Track.{Track_ID, Unquantized}
import application.model.repository.TrackRepository
import application.ports.PlaybackDevice
import application.quantization.TrackQuantizer
import application.quantization.TrackQuantizer.Params
import application.recording.RecordingManager
import javax.inject.{Inject, Named}

import scala.util.{Failure, Success, Try}

// TODO: see if TrackQuantizer and TicksPerQuarter can be moved to a separate service/controller
class TrackController @Inject() (
  trackRepository: TrackRepository,
  quantizer: TrackQuantizer,
  @Named("TicksPerQuarter") recordingResolution: Int,
  playback: ServiceRegistry[PlaybackDevice],
  recordingManager: RecordingManager
) extends Controller {

  override def handle[Res]: PartialFunction[Command[Res], Try[Res]] = {
    case StartRecording => recordingManager.startRecording
    case StoreRecorded(ideaId) => storeRecorded(ideaId)
    case Quantize(trackId, subdivision, gridErrorMultiplier) => quantize(trackId, subdivision, gridErrorMultiplier)
    case Play(trackId) => play(trackId)
    case GetAll => Success(trackRepository.getAll)
  }

  private def storeRecorded(ideaId: Idea_ID): Try[Track] = {
    recordingManager.stopRecording.map { case (ticks, elements) =>
      trackRepository.add(
        ideaId,
        Unquantized,
        ticks,
        elements
      )
    }
  }

  private def play(id: Track_ID): Try[Unit] = {
    trackRepository.get(id).map { track =>
      playback.useAllActive(_.playback(track))
    }
  }

  def quantize(id: Track_ID, subdivision: Int, gridErrorMultiplier: Int): Try[Track] = {
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
      }
  }
}
