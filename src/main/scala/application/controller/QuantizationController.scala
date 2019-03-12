package application.controller

import application.model.Idea
import application.model.repository.TrackRepository
import application.quantization.TrackQuantizer
import javax.inject.Inject

trait QuantizationController {

  def quantize(id: Idea.ID): Unit

}

case class QuantizationControllerImpl @Inject() (
  trackRepository: TrackRepository,
  trackQuantizer: TrackQuantizer
) extends QuantizationController {

  override def quantize(id: Idea.ID): Unit = {
    trackRepository.retrieve(id).foreach { track =>
      // TODO: make sure it stores the track as well
      trackQuantizer.quantizeTrack(track)
    }
  }

}