package application.controller

import application.model.repository.TrackRepository
import application.model.{Idea, Quantized, Unquantized}
import application.quantization.DefaultQuantizer.Params
import application.quantization.TrackSpacingQuantizer
import javax.inject.Inject

trait QuantizationController {

  def quantize(id: Idea.ID, subdivision: Int, gridErrorMultiplier: Int): Unit

}

case class QuantizationControllerImpl @Inject() (
  trackRepository: TrackRepository,
  spacingQuantizer: TrackSpacingQuantizer
) extends QuantizationController {

  override def quantize(id: Idea.ID, subdivision: Int, gridErrorMultiplier: Int): Unit = {
    val quantizationParams = Params(6, 192, gridErrorMultiplier, subdivision)

    trackRepository.retrieve(id,Unquantized)
      .foreach { track =>
        trackRepository.store(
          id,
          Quantized,
          spacingQuantizer.quantize(track,quantizationParams)
        )
      }
  }

}