package application.controller

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

import scala.util.Try

trait TrackController {

  def startRecording: Try[Unit]

  def storeRecorded(idea_ID: Idea_ID): Try[Track]

  def play(track: Track_ID): Boolean

  // TODO: refactor to yield Try[Track] as return type
  def quantize(track: Track_ID, subdivision: Int, gridErrorMultiplier: Int): Option[Track]

  def getAll: Vector[Track]

}

// TODO: see if TrackQuantizer and TicksPerQuarter can be moved to a separate service/controller
class TrackControllerImpl @Inject() (
  trackRepository: TrackRepository,
  quantizer: TrackQuantizer,
  @Named("TicksPerQuarter") recordingResolution: Int,
  playback: ServiceRegistry[PlaybackDevice],
  recordingManager: RecordingManager
) extends TrackController {

  def startRecording: Try[Unit] = {
    recordingManager.startRecording
  }

  def storeRecorded(ideaId: Idea_ID): Try[Track] = {
    recordingManager.stopRecording.map { case (ticks, elements) =>
      trackRepository.add(
        ideaId,
        Unquantized,
        ticks,
        elements
      )
    }
  }

  def play(id: Track_ID): Boolean = {
    trackRepository.get(id) match {
      case None => false
      case Some(data) =>
        playback.useAllActive(_.playback(data))
        true
    }
  }

  def quantize(id: Track_ID, subdivision: Int, gridErrorMultiplier: Int): Option[Track] = {
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

  def getAll: Vector[Track] = trackRepository.getAll

}
