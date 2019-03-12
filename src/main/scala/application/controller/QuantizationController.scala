package application.controller

import application.model.Idea
import application.model.repository.TrackRepository
import application.quantization.TrackSpacingQuantizer
import application.quantization.TrackSpacingQuantizer.Params
import javax.inject.Inject

trait QuantizationController {

  def quantize(id: Idea.ID): Unit

}

case class QuantizationControllerImpl @Inject() (
  trackRepository: TrackRepository,
  spacingQuantizer: TrackSpacingQuantizer
) extends QuantizationController {

  override def quantize(id: Idea.ID): Unit = {
    trackRepository.retrieve(id).foreach { track =>
      // TODO: make sure it stores the track as well
      spacingQuantizer.quantize(track, Params(minGrid = 10, maxGrid =  100, gridErrorWeight =  10))
    }
  }

}