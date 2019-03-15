package application.controller

import application.TicksPerQuarter
import application.component.ServiceRegistry
import application.model.Idea.Idea_ID
import application.model.Track.{Track_ID, Unquantized}
import application.model.repository.TrackRepository
import application.model.{Ticks, Track}
import application.ports.{InputDevice, PlaybackDevice}
import application.quantization.TrackQuantizer
import application.quantization.TrackQuantizer.Params
import javax.inject.Inject

trait TrackController {

  def record(ideaID: Idea_ID): Option[Track]

  def play(track: Track_ID): Boolean

  def quantize(track: Track_ID, subdivision: Int, gridErrorMultiplier: Int): Option[Track]

  def getAll: Vector[Track]

}

class TrackControllerImpl @Inject() (
  trackRepository: TrackRepository,
  quantizer: TrackQuantizer,
  input: ServiceRegistry[InputDevice],
  playback: ServiceRegistry[PlaybackDevice],
  recordingResolution: TicksPerQuarter,
) extends TrackController {

  def record(ideaID: Idea_ID): Option[Track] = {
    // TODO: clean up this hack without using the var (or move to ServiceRegistry)
    var res: Option[Track] = None

    input.use { device =>
      res =
        device.read(recordingResolution.ticks)
          .map(trackRepository.add(ideaID, Unquantized, Ticks(recordingResolution.ticks), _))
          .toOption
    }

    res
  }

  def play(id: Track_ID): Boolean = {
    trackRepository.get(id) match {
      case None => false
      case Some(data) =>
        playback.use(_.playback(data))
        true
    }
  }

  def quantize(id: Track_ID, subdivision: Int, gridErrorMultiplier: Int): Option[Track] = {
    val quantizationParams = Params(
      recordingResolution.ticks / 16,
      recordingResolution.ticks * 2,
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
