package application.controller

import application.model.repository.TrackRepository
import application.model.{Idea, Quantized, Unquantized}
import application.quantization.DefaultQuantizer.Params
import application.quantization.TrackSpacingQuantizer
import javax.inject.Inject

trait QuantizationController {

  def quantize(id: Idea.ID, subdivision: Int): Unit

}

case class QuantizationControllerImpl @Inject() (
  trackRepository: TrackRepository,
  spacingQuantizer: TrackSpacingQuantizer
) extends QuantizationController {

  override def quantize(id: Idea.ID, subdivision: Int): Unit = {
    val quantizationParams = Params(10, 100, 10, subdivision)

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