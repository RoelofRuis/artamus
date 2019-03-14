package application.controller

import application.component.ServiceRegistry
import application.model.Track.{Quantized, TrackType, Unquantized}
import application.model.repository.TrackRepository
import application.model.{ID, Idea}
import application.ports.{InputDevice, PlaybackDevice}
import application.quantization.TrackQuantizer
import application.quantization.TrackQuantizer.Params
import javax.inject.Inject

trait TrackController {

  def record(ideaID: ID[Idea.type]): Unit //ID[Track.type]

  def stopRecording(): Unit

  def play(idea: ID[Idea.type], trackType: TrackType): Boolean

  def quantize(ideaID: ID[Idea.type], subdivision: Int, gridErrorMultiplier: Int): Unit

}

class TrackControllerImpl @Inject() (
  trackRepository: TrackRepository,
  spacingQuantizer: TrackQuantizer,
  input: ServiceRegistry[InputDevice],
  playback: ServiceRegistry[PlaybackDevice],
) extends TrackController {

  private final val TICKS_PER_QUARTER = 96

  def record(ideaID: ID[Idea.type]): Unit = {
    input.use { device =>
      device.read(TICKS_PER_QUARTER)
        .foreach(trackRepository.store(ideaID, Unquantized, _))
    }
  }

  def stopRecording(): Unit = ???

  def play(ideaID: ID[Idea.type], trackType: TrackType): Boolean = {
    trackRepository.retrieve(ideaID, trackType) match {
      case None => false
      case Some(data) =>
        playback.use(_.playback(data))
        true
    }
  }

  override def quantize(ideaID: ID[Idea.type], subdivision: Int, gridErrorMultiplier: Int): Unit = {
    val quantizationParams = Params(6, 192, gridErrorMultiplier, subdivision)

    trackRepository.retrieve(ideaID, Unquantized)
      .foreach { track =>
        trackRepository.store(
          ideaID,
          Quantized,
          spacingQuantizer.quantize(track,quantizationParams)
        )
      }
  }

}
