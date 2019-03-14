package application.controller

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

  def stopRecording(): Unit // TODO: Determine return type

  def play(track: Track_ID): Boolean

  def quantize(track: Track_ID, subdivision: Int, gridErrorMultiplier: Int): Option[Track]

}

class TrackControllerImpl @Inject() (
  trackRepository: TrackRepository,
  quantizer: TrackQuantizer,
  input: ServiceRegistry[InputDevice],
  playback: ServiceRegistry[PlaybackDevice],
) extends TrackController {

  private final val TICKS_PER_QUARTER = 96

  def record(ideaID: Idea_ID): Option[Track] = {
    // TODO: clean up this hack without using the var (or move to ServiceRegistry)
    var res: Option[Track] = None

    input.use { device =>
      res =
        device.read(TICKS_PER_QUARTER)
          .map(trackRepository.add(ideaID, Unquantized, Ticks(TICKS_PER_QUARTER), _))
          .toOption
    }

    res
  }

  def stopRecording(): Unit = ???

  def play(id: Track_ID): Boolean = {
    trackRepository.get(id) match {
      case None => false
      case Some(data) =>
        playback.use(_.playback(data))
        true
    }
  }

  override def quantize(id: Track_ID, subdivision: Int, gridErrorMultiplier: Int): Option[Track] = {
    val quantizationParams = Params(6, 192, gridErrorMultiplier, subdivision)

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
