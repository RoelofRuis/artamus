package application.controller

import application.model.Idea
import application.model.repository.TrackRepository
import application.quantization.Quantizer
import javax.inject.Inject

trait QuantizationController {

  def quantize(id: Idea.ID): Unit

}

case class QuantizationControllerImpl @Inject() (
  trackRepository: TrackRepository,
  quantizer: Quantizer
) extends QuantizationController {

  override def quantize(id: Idea.ID): Unit = {
    trackRepository.retrieveUnquantized(id).foreach { track =>
      trackRepository.storeQuantized(id, quantizer.quantize(track))
    }
  }

}